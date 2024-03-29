// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.2")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.4.4")

// Load testing tool:
// http://gatling.io/docs/2.2.2/extensions/sbt_plugin.html
addSbtPlugin("io.gatling" % "gatling-sbt" % "4.7.0")

// Scala formatting: "sbt scalafmt"
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.15")

addSbtPlugin("net.virtual-void"  % "sbt-dependency-graph"% "0.10.0-RC1")

