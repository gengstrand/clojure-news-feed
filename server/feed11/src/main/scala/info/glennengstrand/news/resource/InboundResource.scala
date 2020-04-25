package info.glennengstrand.news.resource

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.web.{Router, RoutingContext}
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.lang.scala.ScalaLogger
import scala.concurrent.Future
import io.vertx.scala.core.Vertx
import info.glennengstrand.news.service.InMemoryCache
import info.glennengstrand.news.event._

object InboundResource extends NewsFeedResource {
  private val ns = "Inbound::"
  private def getInbound(router: Router, vertx: Vertx): Unit = {
    vertx.deployVerticle(ScalaVerticle.nameForVerticle[GetInboundEvent], genDeployOptions(Topics.GetInbound))
    router
      .get("/participant/:id/inbound")
      .handler(rc => {
        val k = key(ns)
        InMemoryCache.put(k, rc)
        publish(Topics.GetInbound.name, k, vertx)
      })
  }
  def route(router: Router, vertx: Vertx): Unit = {
    getInbound(router, vertx)
  }
}