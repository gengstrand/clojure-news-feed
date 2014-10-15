name := "perf2"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.1.0"

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")

retrieveManaged := true

(unmanagedSourceDirectories in Compile) <<= (scalaSource in Compile)(Seq(_))

(unmanagedSourceDirectories in Test) <<= (scalaSource in Test)(Seq(_))  


