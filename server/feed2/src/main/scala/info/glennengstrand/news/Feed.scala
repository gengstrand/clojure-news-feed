package info.glennengstrand.news

import akka.actor.Actor
import info.glennengstrand.io.{EmptyFactoryClass, FactoryClass}
import spray.routing._
import spray.http._
import spray.http.{StatusCodes, ContentType}
import spray.http.HttpEntity
import MediaTypes._

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

  val myRoute =
    path("participant" / LongNumber) { id =>
      get {
        respondWithMediaType(`application/json`) {
          complete(
            try {
              Feed.factory.getObject("participant", id).get.asInstanceOf[Participant].toJson
            } catch {
              case e: Exception => {
                e.printStackTrace()
                HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage)
              }
            })
        }
      }
    } ~
    path("participant.new") {
      post {
        entity(as[String]) { body =>
          val retVal = Feed.factory.getObject("participant", body).get.asInstanceOf[Participant]
          retVal.save
          respondWithMediaType(`application/json`) {
            complete(retVal.toJson)
          }
        }
      }
    } ~
      path("friends" / LongNumber) { id =>
        get {
          respondWithMediaType(`application/json`) {
            complete(
              try {
                Feed.factory.getObject("friends", id).get.asInstanceOf[Friends].toJson
              } catch {
                case e: Exception => {
                  e.printStackTrace()
                  HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage)
                }
              })
          }
        }
      } ~
      path("friends.new") {
        post {
          entity(as[String]) { body =>
            val retVal = Feed.factory.getObject("friend", body).get.asInstanceOf[Friend]
            retVal.save
            respondWithMediaType(`application/json`) {
              complete(retVal.toJson)
            }
          }
        }
      } ~
      path("inbound" / IntNumber) { id =>
        get {
          respondWithMediaType(`application/json`) {
            complete(
              try {
                Feed.factory.getObject("inbound", id).get.asInstanceOf[InboundFeed].toJson
              } catch {
                case e: Exception => {
                  e.printStackTrace()
                  HttpResponse(StatusCodes.InternalServerError, e.getLocalizedMessage)
                }
              })
          }
        }
      } ~
      path("inbound.new") {
        post {
          entity(as[String]) { body =>
            val retVal = Feed.factory.getObject("inbound", body).get.asInstanceOf[Inbound]
            retVal.save
            respondWithMediaType(`application/json`) {
              complete(retVal.toJson)
            }
          }
        }
      }
}