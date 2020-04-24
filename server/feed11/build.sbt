import sbt.Package._
import sbt._
import Docker.autoImport.exposedPorts

scalaVersion := "2.12.6"

assemblyMergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.contains("netty") => MergeStrategy.first
  case PathList(ps @ _*) if ps.contains("ow2") => MergeStrategy.first
  case PathList(ps @ _*) if ps.contains("commons-logging") => MergeStrategy.first
  case PathList(ps @ _*) if ps.contains("slf4j") => MergeStrategy.first
  case other => MergeStrategy.defaultMergeStrategy(other)
}

enablePlugins(DockerPlugin)
exposedPorts := Seq(8080)

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= Vector (
  Library.vertx_lang_scala,
  Library.vertx_web,
  Library.scalaTest       % "test",
  // Uncomment for clustering
  // Library.vertx_hazelcast,

  //required to get rid of some warnings emitted by the scala-compile
  Library.vertx_codegen
)

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2", 
  "org.slf4j" % "slf4j-nop" % "1.7.25",
  "mysql" % "mysql-connector-java" % "8.0.19",
  "redis.clients" % "jedis" % "3.2.0", 
  "com.datastax.oss" % "java-driver-core" % "4.5.1",
  "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.6.2", 
  "net.sf.ehcache" % "ehcache" % "2.10.6",
)

packageOptions += ManifestAttributes(
  ("Main-Verticle", "scala:info.glennengstrand.news.HttpVerticle"))

