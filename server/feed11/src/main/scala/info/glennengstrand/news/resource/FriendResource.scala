package info.glennengstrand.news.resource

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.web.{Router, RoutingContext}
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.lang.scala.ScalaLogger
import scala.concurrent.Future
import io.vertx.scala.core.Vertx
import info.glennengstrand.news.service.CacheWrapper
import info.glennengstrand.news.event._

object FriendResource extends NewsFeedResource {
  private val ns = "Friend::"
  private def getFriend(router: Router, vertx: Vertx): Unit = {
    vertx.deployVerticle(ScalaVerticle.nameForVerticle[GetFriendsEvent], genDeployOptions(Topics.GetFriend))
    router
      .get("/participant/:id/friends")
      .handler(rc => {
        val k = key(ns)
        CacheWrapper.put(k, rc)
        publish(Topics.GetFriend.name, k, vertx)
      })
  }
  private def createFriend(router: Router, vertx: Vertx): Unit = {
    vertx.deployVerticle(ScalaVerticle.nameForVerticle[CreateFriendEvent], genDeployOptions(Topics.CreateFriend))
    router.post().handler(BodyHandler.create)
    router
      .post("/participant/:id/friends")
      .handler(rc => {
        val k = key(ns)
        CacheWrapper.put(k, rc)
        publish(Topics.CreateFriend.name, k, vertx)
      })
  }
  def route(router: Router, vertx: Vertx): Unit = {
    createFriend(router, vertx)
    getFriend(router, vertx)
  }
}