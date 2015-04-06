package info.glennengstrand.news

import akka.actor.Actor
import info.glennengstrand.io.{EmptyFactoryClass, FactoryClass, PerformanceLogger}
import spray.routing._
import spray.http._
import spray.http.{StatusCodes, ContentType}
import spray.http.HttpEntity
import MediaTypes._
import java.util.logging.{Logger, Level}

object Feed {
  var factory: FactoryClass = new EmptyFactoryClass
}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class FeedActor extends Actor with Feed {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait Feed extends HttpService {
  val log = Logger.getLogger("info.glennengstrand.news.Feed")
  val myRoute =
    path("participant" / LongNumber) { id =>
      get {
        try {
          val before = System.currentTimeMillis()
          val retVal = Feed.factory.getObject("participant", id).get.asInstanceOf[Participant].toJson
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
            val retVal = Feed.factory.getObject("participant", body).get.asInstanceOf[Participant]
            retVal.save
            val after = System.currentTimeMillis()
            Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "participant", "post", after - before)
            respondWithMediaType(`application/json`) {
              complete(retVal.toJson)
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
            val retVal = Feed.factory.getObject("friends", id).get.asInstanceOf[Friends].toJson(Feed.factory)
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
            val before = System.currentTimeMillis()
            try {
              val friend = Feed.factory.getObject("friend", body).get.asInstanceOf[Friend]
              friend.save
              val retVal = friend.toJson(Feed.factory)
              val after = System.currentTimeMillis()
              Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "friends", "post", after - before)
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
            val retVal = Feed.factory.getObject("inbound", id).get.asInstanceOf[InboundFeed].toJson
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
      path("inbound" / "new") {
        post {
          entity(as[String]) { body =>
            val before = System.currentTimeMillis()
            try {
              val retVal = Feed.factory.getObject("inbound", body).get.asInstanceOf[Inbound]
              retVal.save
              val after = System.currentTimeMillis()
              Feed.factory.getObject("logger").get.asInstanceOf[PerformanceLogger].log("feed", "inbound", "post", after - before)
              respondWithMediaType(`application/json`) {
                complete(retVal.toJson)
              }
            } catch {
              case e: Exception => {
                log.log(Level.SEVERE, "cannot upsert inbound\n", e)
                complete(HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage))
              }
            }
          }
        }
      } ~
      path("outbound" / IntNumber) { id =>
        get {
          val before = System.currentTimeMillis()
          try {
            val retVal = Feed.factory.getObject("outbound", id).get.asInstanceOf[OutboundFeed].toJson
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
                  val retVal = "[" + Outbound.lookup(body).map(o => o.toJson).reduce(_ + "," + _) + "]"
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
