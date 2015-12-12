name := "news-feed"

organization  := "info.glennengstrand"

version       := "0.1.0-SNAPSHOT"

scalaVersion  := "2.11.7"

fork in run := true

javaOptions ++= Seq(
  "-Dlog.service.output=/dev/stderr",
  "-Dlog.access.output=/dev/stderr")
  
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

lazy val versions = new {
  val finatra = "2.1.1"
  val guice = "4.0"
  val logback = "1.0.13"
  val mockito = "1.9.5"
  val scalatest = "2.2.3"
  val specs2 = "2.3.12"
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com",
  "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"
)

assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.discard
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case "unwanted.txt"     => MergeStrategy.discard
  case x if x.contains("slf4j") || x.contains("log") => MergeStrategy.last
  case other => MergeStrategy.defaultMergeStrategy(other)
}

libraryDependencies ++= Seq(
    "com.livestream"      %% "scredis"        % "2.0.6",
    "org.scala-lang.modules"  %%  "scala-parser-combinators"  % "1.0.3",
    "mysql"               %   "mysql-connector-java"  % "5.0.8",
    "postgresql"          %   "postgresql"    % "9.1-901.jdbc4",
    "com.datastax.cassandra"  % "cassandra-driver-core" % "2.1.4",
    "org.apache.kafka"    %   "kafka-clients"    % "0.8.2.0",
    "com.mchange"         %   "c3p0"          % "0.9.5",
    "com.dynamicalsoftware"   %   "feed.support.services"   %   "0.0.1-SNAPSHOT", 
    "org.apache.solr"	      %	  "solr-core"		    %	"5.3.1", 
    "org.apache.solr"	      %	  "solr-solrj"		    %	"5.3.1",
  "com.twitter.finatra" %% "finatra-http" % versions.finatra,
  "com.twitter.finatra" %% "finatra-slf4j" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,
  "ch.qos.logback" % "logback-classic" % versions.logback % "test",  
  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test",
  "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",
  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test" classifier "tests",
  "org.mockito" % "mockito-core" % versions.mockito % "test",
  "org.scalatest" %% "scalatest" % versions.scalatest % "test",
  "org.specs2" %% "specs2" % versions.specs2 % "test")


