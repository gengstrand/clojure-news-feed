import sbt.{Def, _}
import sbtassembly.AssemblyPlugin.autoImport.{MergeStrategy, assembly, assemblyMergeStrategy}
import sbtassembly.{AssemblyPlugin, PathList}
import Keys._

object Build extends AutoPlugin {

  override def trigger = allRequirements

  override def requires: Plugins = AssemblyPlugin

  override def projectSettings: Seq[Def.Setting[_]] =
    Vector(
      resolvers ++= Vector(
          "Sonatype SNAPSHOTS" at "https://oss.sonatype.org/content/repositories/snapshots/"
      ),
      scalaVersion := Version.Scala,
      assemblyMergeStrategy in assembly := {
        case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
        case PathList("META-INF", xs @ _*) => MergeStrategy.last
        case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
        case PathList("codegen.json") => MergeStrategy.discard
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      },
      scalacOptions ++= Vector(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-target:jvm-1.8",
        "-encoding", "UTF-8"
      ),
      mainClass := Some("io.vertx.core.Launcher"),
      unmanagedSourceDirectories in Compile := Vector(scalaSource.in(Compile).value),
      unmanagedSourceDirectories in Test := Vector(scalaSource.in(Test).value),
      initialCommands in console := """|import io.vertx.lang.scala._
                                       |import io.vertx.lang.scala.ScalaVerticle.nameForVerticle
                                       |import io.vertx.scala.core._
                                       |import scala.concurrent.Future
                                       |import scala.concurrent.Promise
                                       |import scala.util.Success
                                       |import scala.util.Failure
                                       |val vertx = Vertx.vertx
                                       |implicit val executionContext = io.vertx.lang.scala.VertxExecutionContext(vertx.getOrCreateContext)
                                       |""".stripMargin
    )
}
