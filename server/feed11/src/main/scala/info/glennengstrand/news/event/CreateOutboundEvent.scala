package info.glennengstrand.news.event

import io.vertx.lang.scala.{ScalaVerticle, ScalaLogger}
import io.vertx.scala.ext.web.RoutingContext
import scala.concurrent.Future
import scala.util.{Success, Failure}
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import info.glennengstrand.news.model.{Friend, Inbound, Outbound}
import info.glennengstrand.news.resource.Topics
import info.glennengstrand.news.service.{CacheWrapper, OutboundService, FriendService}

class CreateOutboundEvent extends ScalaVerticle with NewsFeedEvent {
  override def startFuture(): Future[_] = {
    val bus = vertx.eventBus
    bus.consumer[String](Topics.CreateOutbound.name)
      .handler(msg => {
          idbody(msg).foreach(nfr => {
             decode[Outbound](nfr.body) match {
                case Left(d) => {
                  LOGGER.error(d.getLocalizedMessage)
                  end(nfr.rc, 400, "text/plain", d.getLocalizedMessage)
                }
                case Right(p) => {
                  OutboundService.create(p, eo => {
                    eo match {
                      case Success(o) => {
                        end(nfr.rc, 200, "application/json", o.asJson.noSpaces)
                        FriendService.get(nfr.id, ef => {
                          ef match {
                            case Success(friends) => {
                              friends.foreach(f => {
                                val i = Inbound(
                                    from = o.from,
                                    to = f.to,
                                    occurred = o.occurred,
                                    subject = o.subject,
                                    story = o.story
                                    )
                                bus.send(Topics.CreateInbound.name, i.asJson.noSpaces)
                              })
                            }
                            case Failure(e) => {
                              LOGGER.error("cannot fetch friends: ".concat(e.getLocalizedMessage))
                            }
                          }
                        })  
                      }
                      case Failure(e) => {
                        LOGGER.error(e.getLocalizedMessage)
                        end(nfr.rc, 500, "text/plain", e.getLocalizedMessage)
                      }
                    }
                  })
                }
             }
          })
        })
      .completionFuture()
  } 
}