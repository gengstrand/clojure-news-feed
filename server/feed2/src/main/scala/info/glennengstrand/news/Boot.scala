package info.glennengstrand.news

import java.util.logging.Logger

import info.glennengstrand.io._
import scala.util.{Try, Success, Failure}
import java.io.FileInputStream
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.logging.filter.{LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.logging.modules.Slf4jBridgeModule

/** responsible for creating main entity abstraction objects for the real service */
class ServiceFactoryClass extends FactoryClass {
  val performanceLogger = new Kafka
  def isEmpty: Boolean = false
  def getObject(name: String, id: Int): Option[Object] = {
    name match {
      case "inbound" => Some(Inbound(id))
      case "outbound" => Some(Outbound(id))
      case "participant" => Some(Participant(id))
      case "friends" => Some(Friends(id))
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

/** main starting entry point for the real service */
object NewsFeedServerMain extends NewsFeedServer

class NewsFeedServer extends HttpServer {
  val settingsFile = args.length match {
    case 0 => {
    	 val ac = Try(sys.env("APP_CONFIG"))
	 ac match {
	    case Success(cfname) => cfname
	    case Failure(e) => "settings.properties"
	 }
    }
    case _ => args(0)
  }
  IO.settings.load(new FileInputStream(settingsFile))
  if (Feed.factory.isEmpty) {
     Feed.factory = new ServiceFactoryClass
  }
  override def modules = Seq(Slf4jBridgeModule)

  override def defaultFinatraHttpPort = ":8080"

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[Feed]
  }
}