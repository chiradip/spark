import sbt._
import Keys._

object Dep {
  /** Versions - only the versions that has multiple entries or multiple references or very important ones are listed here
    * Other versions can be specifed in inline along witjh the groupid/artifactid - just for the convenience
   */
  object V { /* Means version - get used to it :) */
    val scala       = "2.9.2"
    val slf4j       = "1.6.1"
    // Hadoop version to build against. For example, "0.20.2", "0.20.205.0", or
    // "1.0.4" for Apache releases, or "0.20.2-cdh3u5" for Cloudera Hadoop.
    val hadoop      = "1.0.4"
    // For Hadoop 2 versions such as "2.0.0-mr1-cdh4.1.1", set the hadoopmajor to "2"
    //val hadoop = "2.0.0-mr1-cdh4.1.1"
    //val hadoopmajor = "2"
    val hadoopmajor = "1"

    //Akka has multiple things like akka-actor, akka-remote and akka-slf4j - this will help to keep same version for all
    val akka        = "2.0.3"
    val spray       = "1.0-M2.1"
  }

  val jetty       = "org.eclipse.jetty"     %   "jetty-server"        % "7.5.3.v20111011"
  val scalatest   = "org.scalatest"         %%  "scalatest"           % "1.8"               % "test"
  val scalacheck  = "org.scalacheck"        %%  "scalacheck"          % "1.9"               % "test"
  val novocode    = "com.novocode"          %   "junit-interface"     % "0.8"               % "test"
  val easymock    = "org.easymock"          %   "easymock"            % "3.1"               % "test"

  val guava       = "com.google.guava"      %   "guava"               % "11.0.1"
  val log4j       = "log4j"                 %   "log4j"               % "1.2.16"
  val slf4j       = "org.slf4j"             %   "slf4j-api"           % V.slf4j
  val slf4jlog4j  = "org.slf4j"             %   "slf4j-log4j12"       % V.slf4j
  val ning        = "com.ning"              %   "compress-lzf"        % "0.8.4"
  val hadoopcore  = "org.apache.hadoop"     %   "hadoop-core"         % V.hadoop
  val asm         = "asm"                   %   "asm-all"             % "3.3.1"
  val gprotobuf   = "com.google.protobuf"   %   "protobuf-java"       % "2.4.1"
  val javakaffee  = "de.javakaffee"         %   "kryo-serializers"    % "0.22"
  val akkaactor   = "com.typesafe.akka"     %   "akka-actor"          % V.akka
  val akkaremote  = "com.typesafe.akka"     %   "akka-remote"         % V.akka
  val akkaslf4j   = "com.typesafe.akka"     %   "akka-slf4j"          % V.akka
  val fastutil    = "it.unimi.dsi"          %   "fastutil"            % "6.4.4"
  val colt        = "colt"                  %   "colt"                % "1.2.0"
  val spraycan    = "cc.spray"              %   "spray-can"           % V.spray
  val sprayserver = "cc.spray"              %   "spray-server"        % V.spray
  val sprayjson   = "cc.spray"              %%  "spray-json"          % "1.1.1"
  val mesos       = "org.apache.mesos"      %   "mesos"               % "0.9.0-incubating"

  val flume       = "org.apache.flume"      %   "flume-ng-sdk"        % "1.2.0"             % "compile"
  val zkclient    = "com.github.sgroschupf" %   "zkclient"            % "0.1"
  val twtr4jstrm  = "org.twitter4j"         %   "twitter4j-stream"    % "3.0.3"
  val akka0mq     = "com.typesafe.akka"     %   "akka-zeromq"         % V.akka

  val algebird    = "com.twitter"           %   "algebird-core_2.9.2" % "0.1.8"
}

object Deps {
  import Dep._
  
  def sharedlibs    = Seq(jetty, scalatest, scalacheck, novocode, easymock)
  def corelibs      = Seq(guava, log4j, slf4j, slf4jlog4j, ning, hadoopcore, asm, gprotobuf, javakaffee, akkaactor, 
                          akkaremote, akkaslf4j, fastutil, colt, spraycan, sprayserver, sprayjson, mesos)
  def examplelibs   = Seq(algebird)
  def streaminglibs = Seq(flume, zkclient, twtr4jstrm, akka0mq)

  // Use this to add test specific libraries 
  def testlibs      = Seq(scalatest, scalacheck, easymock)

  //All repositories 
  def repos         = Seq(
    "JBoss Repository"      at "http://repository.jboss.org/nexus/content/repositories/releases/",
    "Spray Repository"      at "http://repo.spray.cc/",
    "Cloudera Repository"   at "https://repository.cloudera.com/artifactory/cloudera-repos/",
    "Twitter4J Repository"  at "http://twitter4j.org/maven2/"
  )
  def akkarepo      = Seq("Akka Repository"     at "http://repo.akka.io/releases/")
  def sonatyperepos = Seq("sonatype-snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots",
                          "sonatype-staging"    at "https://oss.sonatype.org/service/local/staging/deploy/maven2/")

  def pomextra      = (
      <url>http://spark-project.org/</url>
      <licenses>
        <license>
          <name>BSD License</name>
          <url>https://github.com/mesos/spark/blob/master/LICENSE</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <connection>scm:git:git@github.com:mesos/spark.git</connection>
        <url>scm:git:git@github.com:mesos/spark.git</url>
      </scm>
      <developers>
        <developer>
          <id>matei</id>
          <name>Matei Zaharia</name>
          <email>matei.zaharia@gmail.com</email>
          <url>http://www.cs.berkeley.edu/~matei</url>
          <organization>U.C. Berkeley Computer Science</organization>
          <organizationUrl>http://www.cs.berkeley.edu/</organizationUrl>
        </developer>
      </developers>
    )
}

