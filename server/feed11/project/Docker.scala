import sbt.{Def, _}
import sbtassembly.AssemblyPlugin.autoImport.assembly
import sbtdocker.DockerPlugin
import sbtdocker.DockerPlugin.autoImport.{Dockerfile, docker, dockerfile}

object Docker extends AutoPlugin {

  object autoImport {
    lazy val exposedPorts = SettingKey[Seq[Int]]("exposed-ports", "A list of awesome operating systems")
  }

  import autoImport._

  override def trigger = allRequirements

  override def requires: Plugins = DockerPlugin

  override def projectSettings: Seq[Def.Setting[_]] =
    Vector(
      exposedPorts := Seq(8666),
      dockerfile in docker := {
        // The assembly task generates a fat JAR file
        val artifact: File = assembly.value
        val artifactTargetPath = s"/app/${artifact.name}"

        new Dockerfile {
          from("adoptopenjdk/openjdk8:latest")
          add(artifact, artifactTargetPath)
          entryPoint("java", "-jar", artifactTargetPath)
          expose(exposedPorts.value:_*)
        }
      }
    )
}
