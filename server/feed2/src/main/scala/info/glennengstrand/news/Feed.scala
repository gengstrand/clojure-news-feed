package info.glennengstrand.news

import info.glennengstrand.io.{IO, MicroServiceSerializable, EmptyFactoryClass, FactoryClass, PerformanceLogger}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import org.slf4j.Logger;
import org.slf4j.LoggerFactory
import java.net.URLDecoder
import scala.util.{Try, Success, Failure}
import com.twitter.util.{Await, Future}

/** where to hold  the global class factory */
object Feed {
  var factory: FactoryClass = new EmptyFactoryClass
  val messageTopic = "feed"
  val participantEntity = "participant"
  val outboundEntity = "outbound"
  val inboundEntity = "inbound"
  val friendEntity = "friends"
  val getOperation = "get"
  val postOperation = "post"
  val searchOperation = "search"
}

/** provides routings for mapping requests to the code that processes the requests */
class Feed extends Controller {
  val log = LoggerFactory.getLogger("info.glennengstrand.news.Feed")
  get("/participant/:id") { request: Request => {
    val r = Try {
      val f = IO.workerPool {
        val before = System.currentTimeMillis()
        val retVal = Feed.factory.getObject("participant", request.params("id").toInt).get.asInstanceOf[MicroServiceSerializable].toJson
        val after = System.currentTimeMillis()
        Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log(Feed.messageTopic, Feed.participantEntity, Feed.getOperation, after - before)
        retVal
      }
      Await.result(f)
    }
    r match {
      case Success(rv) => rv
      case Failure(e) => {
        log.warn("cannot fetch participant: ", e)
        e.getLocalizedMessage()
      }
    }
  }}
  post("/participant") { request: Request => {
    val before = System.currentTimeMillis()
    val r = Try {
      val body = URLDecoder.decode(request.contentString, "UTF-8")
      val f = IO.workerPool {
        val p = Feed.factory.getObject("participant", body).get.asInstanceOf[Participant]
        val retVal = "[" + p.save.toJson + "]"
        val after = System.currentTimeMillis()
        Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log(Feed.messageTopic, Feed.participantEntity, Feed.postOperation, after - before)
        log.debug(retVal)
        retVal
      }
      Await.result(f)
    }
    r match {
      case Success(rv) => rv
      case Failure(e) => {
        log.warn("cannot upsert participant: ", e)
        e.getLocalizedMessage()
      }
    }
  }}
  get("/participant/:id/friends") { request: Request => {
    val r = Try {
      val f = IO.workerPool {
        val before = System.currentTimeMillis()
        val retVal = Feed.factory.getObject("friends", request.params("id").toInt).get.asInstanceOf[MicroServiceSerializable].toJson(Feed.factory)
        val after = System.currentTimeMillis()
        Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log(Feed.messageTopic, Feed.friendEntity, Feed.getOperation, after - before)
        retVal
      }
      Await.result(f)
    }
    r match {
      case Success(rv) => rv
      case Failure(e) => {
        log.warn("cannot fetch friends: ", e)
        e.getLocalizedMessage()
      }
    }
  }}
  post("/participant/:id/friends") { request: Request => {
    val r = Try {
      val body = URLDecoder.decode(request.contentString, "UTF-8")
      val f = IO.workerPool {
        val before = System.currentTimeMillis()
        val friend = Feed.factory.getObject("friend", body).get.asInstanceOf[Friend]
        val f = friend.save
        val retVal = f.toJson(Feed.factory)
        val after = System.currentTimeMillis()
        Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log(Feed.messageTopic, Feed.friendEntity, Feed.postOperation, after - before)
        log.debug(retVal)
        retVal
      }
      Await.result(f)
    }
    r match {
      case Success(rv) => rv
      case Failure(e) => {
        log.warn("cannot upsert friends: ", e)
        e.getLocalizedMessage()
      }
    }
  }}
  get("/participant/:id/inbound") { request: Request => {
    val r = Try {
      val f = IO.workerPool {
        val before = System.currentTimeMillis()
        val retVal = Feed.factory.getObject("inbound", request.params("id").toInt).get.asInstanceOf[MicroServiceSerializable].toJson
        val after = System.currentTimeMillis()
        Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log(Feed.messageTopic, Feed.inboundEntity, Feed.getOperation, after - before)
        retVal
      }
      Await.result(f)
    }
    r match {
      case Success(rv) => rv
      case Failure(e) => {
        log.warn("cannot fetch inbound: ", e)
        e.getLocalizedMessage()
      }
    }
  }}
  get("/participant/:id/outbound") { request: Request => {
    val r = Try {
      val f = IO.workerPool {
        val before = System.currentTimeMillis()
        val retVal = Feed.factory.getObject("outbound", request.params("id").toInt).get.asInstanceOf[MicroServiceSerializable].toJson
        val after = System.currentTimeMillis()
        Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log(Feed.messageTopic, Feed.outboundEntity, Feed.getOperation, after - before)
        retVal
      }
      Await.result(f)
    }
    r match {
      case Success(rv) => rv
      case Failure(e) => {
        log.warn("cannot fetch outbound: ", e)
        e.getLocalizedMessage()
      }
    }
  }}
  post("/participant/:id/outbound") { request: Request => {
    val r = Try {
      val body = URLDecoder.decode(request.contentString, "UTF-8")
      val f = IO.workerPool {
        val before = System.currentTimeMillis()
        val retVal = Feed.factory.getObject("outbound", body).get.asInstanceOf[Outbound]
        retVal.save
        val after = System.currentTimeMillis()
        Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log(Feed.messageTopic, Feed.outboundEntity, Feed.postOperation, after - before)
        retVal.toJson
      }
      Await.result(f)
    }
    r match {
      case Success(rv) => rv
      case Failure(e) => {
        log.warn("cannot upsert outbound: ", e)
        e.getLocalizedMessage()
      }
    }
  }}
  get("/outbound") { request: Request => {
    val before = System.currentTimeMillis()
    val r = Try {
      val terms = request.params("keywords")
      val f = IO.workerPool {
      val results = Outbound.lookup(terms)
      val retVal = results.isEmpty match {
        case true => "[]"
        case _ => "[" + results.map(s => Link.toLink(s.toString().toLong)).reduce(_ + "," + _) + "]"
      }
      val after = System.currentTimeMillis()
      Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log(Feed.messageTopic, Feed.outboundEntity, Feed.searchOperation, after - before)
      retVal
      }
      Await.result(f)
    }
    r match {
      case Success(rv) => rv
      case Failure(e) => {
        log.warn("cannot search outbound: ", e)
        e.getLocalizedMessage()
      }
    }
  }}
}
