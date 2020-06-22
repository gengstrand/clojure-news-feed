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
    logger.trace("process: ")
    decode[Outbound](request.body.toString) match {
      case Left(d) => {
        logger.trace("invalid")
        Future {play.api.mvc.Results.Status(400)}
      }
      case Right(p) => outboundService.create(id, p) map {
        rv => Ok(rv.asJson.noSpaces)
      }
    }
  }

  def get(id: Int): Action[AnyContent] = NewsAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
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