package info.glennengstrand.news

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.web.{Router, RoutingContext}
import io.vertx.lang.scala.ScalaLogger
import scala.concurrent.Future
import info.glennengstrand.news.resource._

class HttpVerticle extends ScalaVerticle {
  val LOGGER = ScalaLogger.getLogger(classOf[HttpVerticle].getCanonicalName)
  override def startFuture(): Future[_] = {
    sys.addShutdownHook {
      vertx.deploymentIDs().foreach(id => {
        vertx.undeploy(id)
      })
      info.glennengstrand.news.dao.MySqlDao.db.close()
      info.glennengstrand.news.service.CacheWrapper.shutdown
    }
    val router = Router.router(vertx)
    ParticipantResource.route(router, vertx)
    FriendResource.route(router, vertx)
    InboundResource.route(router, vertx)
    OutboundResource.route(router, vertx)
    vertx
      .createHttpServer()
      .requestHandler(router.accept _)
      .listenFuture(8080, "0.0.0.0")
  }
}