package info.glennengstrand.news

import java.util.logging.Logger

import akka.actor.{ActorSystem, Props, Actor, DeadLetter}
import akka.io.IO
import info.glennengstrand.io._
import info.glennengstrand.news._
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import java.util.Properties
import java.io.FileInputStream

class ServiceFactoryClass extends FactoryClass {
  val performanceLogger = new Kafka
  def getObject(name: String, id: Long): Option[Object] = {
    name match {
      case "participant" => Some(Participant(id))
      case "friends" => Some(Friends(id))
      case _ => None
    }
  }
  def getObject(name: String, id: Int): Option[Object] = {
    name match {
      case "inbound" => Some(Inbound(id))
      case "outbound" => Some(Outbound(id))
      case _ => None
    }
  }
  def getObject(name: String, state: String): Option[Object] = {
    name match {
      case "participant" => Some(Participant(state))
      case "friend" => Some(Friends(state))
      case "inbound" => Some(Inbound(state))
      case "outbound" => Some(Outbound(state))
      case _ => None
    }
  }
  def getObject(name: String): Option[Object] = {
    name match {
      case "logger" => Some(performanceLogger)
      case _ => None
    }
  }
}

object Boot extends App {

  implicit val system = ActorSystem("on-spray-can")

  val settingsFile = args.length match {
    case 0 => "settings.properties"
    case _ => args(0)
  }
  info.glennengstrand.io.IO.settings.load(new FileInputStream(settingsFile))
  val service = system.actorOf(Props[FeedActor], "news-feed-service")
  val listener = system.actorOf(Props[Listener], "dead-letter-listener")
  system.eventStream.subscribe(listener, classOf[DeadLetter])
  implicit val timeout = Timeout(60.seconds)
  Feed.factory = new ServiceFactoryClass
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}

  class Listener extends Actor {
  val log = Logger.getLogger("info.glennengstrand.news.Listener")
  def actorRefFactory = context

    def receive = {
      case DeadLetter(msg, from, to) => log.warning(msg.toString)
    }
  }