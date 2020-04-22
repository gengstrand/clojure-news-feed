package info.glennengstrand.news.event

import io.vertx.lang.scala.ScalaLogger
import io.vertx.scala.ext.web.RoutingContext
import io.vertx.scala.core.eventbus.Message
import info.glennengstrand.news.service.CacheWrapper
import info.glennengstrand.news.resource.Topics

case class NewsFeedRequest(id: Int, body: String, rc: RoutingContext)

trait NewsFeedEvent {
  protected val LOGGER = ScalaLogger.getLogger(classOf[NewsFeedEvent].getCanonicalName)
  private def getBodyInner(rc: RoutingContext): String = {
    rc.getBodyAsString() match {
      case Some(rcb) => {
        rcb
      }
      case None => {
        null
      }
    }
  }
  private def request(msg: Message[String]): NewsFeedRequest = {
    val k = msg.body()
    CacheWrapper.get(k) match {
      case Some(orc) => {
        CacheWrapper.del(k)
        val rc = orc.asInstanceOf[RoutingContext]
        val oid = rc.request.getParam("id")
        oid match {
          case Some(id) => {
            NewsFeedRequest(id.toInt, getBodyInner(rc), rc)
          }
          case None => {
            NewsFeedRequest(0, getBodyInner(rc), rc)
          }
        }
      }
      case None => {
        LOGGER.error("cache key missing")
        null
      }
    }
  }
  protected def end( rc: RoutingContext, status: Int, contentType: String, body: String): Unit = {
    val r = rc.response
    r.setStatusCode(status)
    r.putHeader("content-type", contentType);
    r.end(body)
  }
  protected def id(msg: Message[String]): Option[NewsFeedRequest] = {
    val rv = request(msg)
    if (rv == null) {
      None
    } else {
      if (rv.id == 0) {
        val d = "id is missing"
        LOGGER.error(d)
        end(rv.rc, 400, "text/plain", d)
        None
      } else {
        Some(rv)
      }
    }
  }
  protected def body(msg: Message[String]): Option[NewsFeedRequest] = {
    val rv = request(msg)
    if (rv == null) {
      None
    } else {
      if (rv.body == null) {
        val d = "body is missing"
        LOGGER.error(d)
        end(rv.rc, 400, "text/plain", d)
        None
      } else {
        Some(rv)
      }
    }
  }
  protected def idbody(msg: Message[String]): Option[NewsFeedRequest] = {
    val rv = request(msg)
    if (rv == null) {
      None
    } else {
      if (rv.body == null) {
        val d = "body is missing"
        LOGGER.error(d)
        end(rv.rc, 400, "text/plain", d)
        None
      } else {
        if (rv.id == 0) {
          val d = "id is missing"
          LOGGER.error(d)
          end(rv.rc, 400, "text/plain", d)
          None
        } else {
          Some(rv)
        }
      }
    }
  }
}