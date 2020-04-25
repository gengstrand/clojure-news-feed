package info.glennengstrand.news.event

import io.vertx.lang.scala.{ScalaVerticle, ScalaLogger}
import io.vertx.scala.ext.web.RoutingContext
import scala.concurrent.Future
import scala.util.{Success, Failure}
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import info.glennengstrand.news.model.Outbound
import info.glennengstrand.news.resource.Topics
import info.glennengstrand.news.service.{InMemoryCache, OutboundService}

class SearchOutboundEvent extends ScalaVerticle with NewsFeedEvent {
  override def startFuture(): Future[_] = {
      vertx
        .eventBus()
        .consumer[String](Topics.SearchOutbound.name)
        .handler(msg => {
          val k = msg.body()
          InMemoryCache.get(k) match {
            case Some(orc) => {
              InMemoryCache.del(k)
              val rc = orc.asInstanceOf[RoutingContext]
              val ok = rc.request.getParam("keywords")
              ok match {
                case Some(k) => {
                  OutboundService.search(k, tsr => {
                    tsr match {
                      case Success(sr) => {
                        end(rc, 200, "application/json", sr.asJson.noSpaces)
                      }
                      case Failure(e) => {
                        LOGGER.error(e.getLocalizedMessage)
                        end(rc, 500, "text/plain", e.getLocalizedMessage)
                      }
                    }
                  })
                }
                case None => {
                  val d = "no keywords on search"
                  LOGGER.warn(d)
                  end(rc, 400, "text/plain", d)
                }
              }
            }
            case None => {
              LOGGER.error("routing context not found for this request")
            }
          }
        })
        .completionFuture()
  } 
}