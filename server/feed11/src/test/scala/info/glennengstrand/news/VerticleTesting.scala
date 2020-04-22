package info.glennengstrand.news

import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.lang.scala.{ScalaVerticle, VertxExecutionContext}
import io.vertx.scala.core.{DeploymentOptions, Vertx}
import org.scalatest.{AsyncFlatSpec, BeforeAndAfter}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.reflect.runtime.universe._
import scala.util.{Failure, Success}

abstract class VerticleTesting[A <: ScalaVerticle: TypeTag] extends AsyncFlatSpec with BeforeAndAfter{
  val vertx = Vertx.vertx
  implicit val vertxExecutionContext = VertxExecutionContext(
    vertx.getOrCreateContext()
  )

  private var deploymentId = ""

  def config(): JsonObject = Json.emptyObj()

  before {
    deploymentId = Await.result(
      vertx
        .deployVerticleFuture("scala:" + implicitly[TypeTag[A]].tpe.typeSymbol.fullName,
          DeploymentOptions().setConfig(config()))
        .andThen {
          case Success(d) => d
          case Failure(t) => throw new RuntimeException(t)
        },
      10000 millis
    )
  }

  after {
    Await.result(
      vertx.undeployFuture(deploymentId)
        .andThen {
          case Success(d) => d
          case Failure(t) => throw new RuntimeException(t)
        },
      10000 millis
    )
  }

}
