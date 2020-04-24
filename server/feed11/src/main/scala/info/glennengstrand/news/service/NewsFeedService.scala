package info.glennengstrand.news.service

import io.vertx.lang.scala.ScalaLogger

trait NewsFeedService {
  protected val LOGGER = ScalaLogger.getLogger("NewsFeedService")
}