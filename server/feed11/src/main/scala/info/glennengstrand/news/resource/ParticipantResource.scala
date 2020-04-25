package info.glennengstrand.news.resource

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.web.{Router, RoutingContext}
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.lang.scala.ScalaLogger
import scala.concurrent.Future
import io.vertx.scala.core.Vertx
import info.glennengstrand.news.service.InMemoryCache
import info.glennengstrand.news.event._

object ParticipantResource extends NewsFeedResource {
  private val ns = "Participant::"
  private def getParticipant(router: Router, vertx: Vertx): Unit = {
    vertx.deployVerticle(ScalaVerticle.nameForVerticle[GetParticipantEvent], genDeployOptions(Topics.GetParticipant))
    router
      .get("/participant/:id")
      .handler(rc => {
        val k = key(ns)
        InMemoryCache.put(k, rc)
        publish(Topics.GetParticipant.name, k, vertx)
      })
  }
  private def createParticipant(router: Router, vertx: Vertx): Unit = {
    vertx.deployVerticle(ScalaVerticle.nameForVerticle[CreateParticipantEvent], genDeployOptions(Topics.CreateParticipant))
    router.post().handler(BodyHandler.create)
    router
      .post("/participant")
      .handler(rc => {
        val k = key(ns)
        InMemoryCache.put(k, rc)
        publish(Topics.CreateParticipant.name, k, vertx)
      })
  }
  def route(router: Router, vertx: Vertx): Unit = {
    createParticipant(router, vertx)
    getParticipant(router, vertx)
  }
}