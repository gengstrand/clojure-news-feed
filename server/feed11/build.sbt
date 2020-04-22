import sbt.Package._
import sbt._
import Docker.autoImport.exposedPorts

scalaVersion := "2.12.6"

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
	"net.sf.ehcache" % "ehcache" % "2.10.6"
)

packageOptions += ManifestAttributes(
  ("Main-Verticle", "scala:info.glennengstrand.news.HttpVerticle"))

