package info.glennengstrand.news.event

import io.vertx.lang.scala.{ScalaVerticle, ScalaLogger}
import io.vertx.scala.ext.web.RoutingContext
import scala.concurrent.Future
import scala.util.{Success, Failure}
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import info.glennengstrand.news.model.Inbound
import info.glennengstrand.news.resource.Topics
import info.glennengstrand.news.service.{CacheWrapper, InboundService}

class CreateInboundEvent extends ScalaVerticle with NewsFeedEvent {
  override def startFuture(): Future[_] = {
    vertx
      .eventBus()
      .consumer[String](Topics.CreateInbound.name)
      .handler(msg => {
         decode[Inbound](msg.body) match {
            case Left(d) => {
              LOGGER.error("cannot decode inbound: ".concat(d.getLocalizedMessage))
            }
            case Right(i) => {
              InboundService.create(i, ep => {
                ep match {
                  case Success(op) => {
                    LOGGER.debug("inbound successfully created")
                  }
                  case Failure(e) => {
                    LOGGER.error("cannot save inbound: ".concat(e.getLocalizedMessage))
                  }
                }
              })
            }
         }
      })
      .completionFuture()
  } 
}