organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "com.livestream"      %% "scredis"        % "2.0.6",
    "org.scala-lang.modules"  %%  "scala-parser-combinators"  % "1.0.3",
    "mysql"               %   "mysql-connector-java"  % "5.0.8",
    "com.datastax.cassandra"  % "cassandra-driver-core" % "2.1.4",
    "com.mchange"         %   "c3p0"          % "0.9.5"
  )
}

Revolver.settings
