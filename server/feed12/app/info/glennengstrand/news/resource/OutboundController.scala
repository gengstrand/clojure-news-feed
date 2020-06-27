package info.glennengstrand.news.resource

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import play.api._
import play.api.mvc._
import info.glennengstrand.news.model._
import scala.util.{Success, Failure}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Takes HTTP requests and produces JSON.
  */
class OutboundController @Inject()(cc: NewsControllerComponents)(
    implicit ec: ExecutionContext)
    extends NewsBaseController(cc) {

  private val logger = Logger(getClass)

  def create(id: Int): Action[AnyContent] = NewsAction.async { implicit request =>
    request.body.asJson match {
      case Some(rbj) => {
        val occurred = (rbj \ "occurred").asOpt[String]
        val ob = for {
          from <- (rbj \ "from").asOpt[String]
          subject <- (rbj \ "subject").asOpt[String]
          story <- (rbj \ "story").asOpt[String]
        } yield Outbound(Option(from), occurred, Option(subject), Option(story))
        ob.isEmpty match {
          case false => outboundService.create(id, ob.head) map {
            rv => Ok(rv.asJson.noSpaces)
          }
          case true => Future(BadRequest("from, subject, and story attributes are mandatory"))
        }
      }
      case None => Future(BadRequest("empty request body"))
    }
  }

  def get(id: Int): Action[AnyContent] = NewsAction.async {
    implicit request =>
      outboundService.lookup(id).map { p =>
        Ok(p.asJson.noSpaces)
      }
  }
  
  def search: Action[AnyContent] = NewsAction.async {
    implicit request => 
      request.queryString.get("keywords").map {
        k => outboundService.search(k.head).map { s => 
          Ok(s.asJson.noSpaces)
        }
      }.get
  }

}
