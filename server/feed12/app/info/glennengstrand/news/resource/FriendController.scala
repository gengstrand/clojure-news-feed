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
class FriendController @Inject()(cc: NewsControllerComponents)(
    implicit ec: ExecutionContext)
    extends NewsBaseController(cc) {

  private val logger = Logger(getClass)

  def create(id: Int): Action[AnyContent] = NewsAction.async { implicit request =>
    request.body.asJson match {
      case Some(rbj) => {
        val f = for {
          from <- (rbj \ "from").asOpt[String]
          to <- (rbj \ "to").asOpt[String]
        } yield Friend(None, Option(from), Option(to))
        f.isEmpty match {
          case false => friendService.create(id, f.head) map {
            rv => Ok(rv.asJson.noSpaces)
          }
          case true => Future(BadRequest("from and to attributes are mandatory"))
        }
      }
      case None => Future(BadRequest("empty request body"))
    }
  }

  def get(id: Int): Action[AnyContent] = NewsAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      friendService.lookup(id).map { p =>
        Ok(p.asJson.noSpaces)
      }
  }

}
