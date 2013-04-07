import sbt._
import sbt.Classpaths.publishTask
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import twirl.sbt.TwirlPlugin._

import Dep._
import Deps._
// For Sonatype publishing
//import com.jsuereth.pgp.sbtplugin.PgpKeys._

object SparkBuild extends Build {

  lazy val root = Project("root", file("."), settings = rootSettings) aggregate(core, repl, examples, bagel, streaming)
  lazy val core = Project("core", file("core"), settings = coreSettings)
  lazy val repl = Project("repl", file("repl"), settings = replSettings) dependsOn (core) dependsOn (streaming)
  lazy val examples = Project("examples", file("examples"), settings = examplesSettings) dependsOn (core) dependsOn (streaming)
  lazy val bagel = Project("bagel", file("bagel"), settings = bagelSettings) dependsOn (core)
  lazy val streaming = Project("streaming", file("streaming"), settings = streamingSettings) dependsOn (core)

  // A configuration to set an alternative publishLocalConfiguration
  lazy val MavenCompile = config("m2r") extend(Compile)
  lazy val publishLocalBoth = TaskKey[Unit]("publish-local", "publish local for m2 and ivy")

  def sharedSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.spark-project",
    version := "0.7.1-SNAPSHOT",
    scalaVersion := V.scala,
    scalacOptions := Seq("-unchecked", "-optimize", "-deprecation"),
    unmanagedJars in Compile <<= baseDirectory map { base => (base / "lib" ** "*.jar").classpath },
    retrieveManaged := true,
    retrievePattern := "[type]s/[artifact](-[revision])(-[classifier]).[ext]",
    transitiveClassifiers in Scope.GlobalScope := Seq("sources"),
    testListeners <<= target.map(t => Seq(new eu.henkelmann.sbt.JUnitXmlTestsListener(t.getAbsolutePath))),

    // shared between both core and streaming.
    resolvers ++= akkarepo,
    // For Sonatype publishing
    resolvers ++= sonatyperepos,
    publishMavenStyle := true,
    //useGpg in Global := true,
    pomExtra := Deps.pomextra,
    /* publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("sonatype-snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("sonatype-staging"  at nexus + "service/local/staging/deploy/maven2")
    },*/
    libraryDependencies ++= Deps.sharedlibs,
    parallelExecution := false,
    /* Workaround for issue #206 (fixed after SBT 0.11.0) */
    watchTransitiveSources <<= Defaults.inDependencies[Task[Seq[File]]](watchSources.task,
      const(std.TaskExtra.constant(Nil)), aggregate = true, includeRoot = true) apply { _.join.map(_.flatten) },

    otherResolvers := Seq(Resolver.file("dotM2", file(Path.userHome + "/.m2/repository"))),
    publishLocalConfiguration in MavenCompile <<= (packagedArtifacts, deliverLocal, ivyLoggingLevel) map {
      (arts, _, level) => new PublishConfiguration(None, "dotM2", arts, Seq(), level)
    },
    publishMavenStyle in MavenCompile := true,
    publishLocal in MavenCompile <<= publishTask(publishLocalConfiguration in MavenCompile, deliverLocal),
    publishLocalBoth <<= Seq(publishLocal in MavenCompile, publishLocal).dependOn
  )

  def coreSettings = sharedSettings ++ Seq(
    name := "spark-core",
    resolvers ++= Deps.repos,

    libraryDependencies ++= Deps.corelibs ++ (if (V.hadoopmajor == "2") Some("org.apache.hadoop" % "hadoop-client" % V.hadoop) else None).toSeq,
    unmanagedSourceDirectories in Compile <+= baseDirectory{ _ / ("src/hadoop" + V.hadoopmajor + "/scala") }
  ) ++ assemblySettings ++ extraAssemblySettings ++ Twirl.settings

  def rootSettings = sharedSettings ++ Seq(
    publish := {}
  )

  def replSettings = sharedSettings ++ Seq(
    name := "spark-repl",
    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)
  )

  def examplesSettings = sharedSettings ++ Seq(
    name := "spark-examples",
    libraryDependencies ++= Deps.examplelibs
  )

  def bagelSettings = sharedSettings ++ Seq(name := "spark-bagel")

  def streamingSettings = sharedSettings ++ Seq(
    name := "spark-streaming",
    libraryDependencies ++= Deps.streaminglibs) ++ assemblySettings ++ extraAssemblySettings

  def extraAssemblySettings() = Seq(test in assembly := {}) ++ Seq(
    mergeStrategy in assembly := {
      case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
      case "reference.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    }
  )
}
