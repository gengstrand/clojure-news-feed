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
class ParticipantController @Inject()(cc: NewsControllerComponents)(
    implicit ec: ExecutionContext)
    extends NewsBaseController(cc) {

  private val logger = Logger(getClass)

  def create: Action[AnyContent] = NewsAction.async { implicit request =>
    request.body.asJson match {
      case Some(rbj) => {
        (rbj \ "name").asOpt[String] match {
            case Some(n) => {
              val p = Participant(
                  id = None,
                  name = Some(n),
                  link = None
              )
              participantService.create(p) map ( rv =>  {
                Ok(rv.asJson.noSpaces)          
              })
            }
            case None => Future(BadRequest("participant name is mandatory"))
        }
      }
      case None => Future(BadRequest("empty request body"))
    }
  }

  def get(id: Int): Action[AnyContent] = NewsAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      participantService.lookup(id).map { p =>
        Ok(p.asJson.noSpaces)
      }
  }

}
