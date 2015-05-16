package info.glennengstrand.news

import akka.actor.Actor
import info.glennengstrand.io.{MicroServiceSerializable, EmptyFactoryClass, FactoryClass, PerformanceLogger}
import spray.routing._
import spray.http._
import spray.http.{StatusCodes, ContentType}
import spray.http.HttpEntity
import MediaTypes._
import java.util.logging.{Logger, Level}

/** where to hold  the global class factory */
object Feed {
  var factory: FactoryClass = new EmptyFactoryClass
}

/** spray actor for servicing requests */
class FeedActor extends Actor with Feed {

  def actorRefFactory = context

  def receive = runRoute(myRoute)
}

/** provides routings for mapping requests to the code that processes the requests */
trait Feed extends HttpService {
  val log = Logger.getLogger("info.glennengstrand.news.Feed")
  val myRoute =
    path("participant" / LongNumber) { id =>
      get {
        try {
          val before = System.currentTimeMillis()
          val retVal = Feed.factory.getObject("participant", id).get.asInstanceOf[MicroServiceSerializable].toJson
          val after = System.currentTimeMillis()
          Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "participant", "get", after - before)
          respondWithMediaType(`application/json`) {
            complete(retVal)
          }
        } catch {
          case e: Exception => {
            log.log(Level.SEVERE, "cannot upsert friends\n", e)
            complete(HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage))
          }
        }
      }
    } ~
    path("participant" / "new") {
      post {
        entity(as[String]) { body =>
          val before = System.currentTimeMillis()
          try {
            log.finest("create participant: " + body)
            val p = Feed.factory.getObject("participant", body).get.asInstanceOf[Participant]
            val retVal = "[" + p.save.toJson + "]"
            val after = System.currentTimeMillis()
            Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "participant", "post", after - before)
            log.finest(retVal)
            respondWithMediaType(`application/json`) {
              complete(retVal)
            }
          } catch {
            case e: Exception => {
              log.log(Level.SEVERE, "cannot upsert participant\n", e)
              complete(HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage))
            }
          }
        }
      }
    } ~
      path("friends" / LongNumber) { id =>
        get {
          val before = System.currentTimeMillis()
          try {
            val retVal = Feed.factory.getObject("friends", id).get.asInstanceOf[MicroServiceSerializable].toJson(Feed.factory)
            val after = System.currentTimeMillis()
            Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "friends", "get", after - before)
            respondWithMediaType(`application/json`) {
              complete(retVal)
            }
          } catch {
            case e: Exception => {
              log.log(Level.SEVERE, "cannot fetch friends\n", e)
              complete(HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage))
            }
          }
        }
      } ~
      path("friends" / "new") {
        post {
          entity(as[String]) { body =>
	          log.finest("create friends: " + body)
            val before = System.currentTimeMillis()
            try {
              val friend = Feed.factory.getObject("friend", body).get.asInstanceOf[Friend]
              val f = friend.save
              val retVal = f.toJson(Feed.factory)
              val after = System.currentTimeMillis()
              Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "friends", "post", after - before)
              log.finest(retVal)
              respondWithMediaType(`application/json`) {
                complete(retVal)
              }
            } catch {
              case e: Exception => {
                log.log(Level.SEVERE, "cannot upsert friends\n", e)
                complete(HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage))
              }
            }
          }
        }
      } ~
      path("inbound" / IntNumber) { id =>
        get {
          val before = System.currentTimeMillis()
          try {
            val retVal = Feed.factory.getObject("inbound", id).get.asInstanceOf[MicroServiceSerializable].toJson
            val after = System.currentTimeMillis()
            Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "inbound", "get", after - before)
            respondWithMediaType(`application/json`) {
              complete(retVal)
            }
          } catch {
            case e: Exception => {
              log.log(Level.SEVERE, "cannot fetch inbound\n", e)
              complete(HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage))
            }
          }
        }
      } ~
      path("outbound" / IntNumber) { id =>
        get {
          val before = System.currentTimeMillis()
          try {
            val retVal = Feed.factory.getObject("outbound", id).get.asInstanceOf[MicroServiceSerializable].toJson
            val after = System.currentTimeMillis()
            Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "outbound", "get", after - before)
            respondWithMediaType(`application/json`) {
              complete(retVal)
            }
          } catch {
            case e: Exception => {
              log.log(Level.SEVERE, "cannot fetch outbound\n", e)
              complete(HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage))
            }
          }
        }
      } ~
      path("outbound" / "new") {
        post {
          entity(as[String]) { body =>
            log.finest("create outbound: " + body)
            val before = System.currentTimeMillis()
            try {
              val retVal = Feed.factory.getObject("outbound", body).get.asInstanceOf[Outbound]
              retVal.save
              val after = System.currentTimeMillis()
              Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "outbound", "post", after - before)
              respondWithMediaType(`application/json`) {
                complete(retVal.toJson)
              }
            } catch {
              case e: Exception => {
                log.log(Level.SEVERE, "cannot upsert outbound\n", e)
                complete(HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage))
              }
            }
          }
        }
      } ~
      path("outbound" / "search") {
        post {
          entity(as[String]) { body =>
            val before = System.currentTimeMillis()
            try {
              val results = Outbound.lookup(body).map(o => o.toJson)
              val retVal = results.isEmpty match {
                case true => "[]"
              case _ => "[" + results.reduce(_ + "," + _) + "]"
              }
              val after = System.currentTimeMillis()
              Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "outbound", "search", after - before)
              respondWithMediaType(`application/json`) {
                complete(retVal)
              }
            } catch {
              case e: Exception => {
                log.log(Level.SEVERE, "cannot search outbound\n", e)
                complete(HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage))
              }
            }
          }
        }
      }
}
