name := "news-feed-performance"
 
version := "1.0"
 
scalaVersion := "2.11.7"
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.4.1", 
    "org.apache.kafka"    %   "kafka-clients"    % "0.9.0.1",
	"org.scalaj" %% "scalaj-http" % "2.2.1",
	"joda-time" % "joda-time" % "2.9.2", 
	"org.scalactic" %% "scalactic" % "2.2.6",
	"org.scalatest" %% "scalatest" % "2.2.6" % "test")
	
