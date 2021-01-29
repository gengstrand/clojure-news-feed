package info.glennengstrand.news.service

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

import info.glennengstrand.news.model._
import info.glennengstrand.news.dao._
import info.glennengstrand.news.resource._
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import play.api.{Logger, MarkerContext}

class ParticipantService @Inject()(
    routerProvider: Provider[ParticipantRouter],
    participantDao: ParticipantDao, 
    cache: CacheWrapper)(implicit ec: ExecutionContext) {
  private val logger = Logger(this.getClass)
  def create(postInput: Participant)(
      implicit mc: MarkerContext): Future[Participant] = {
    participantDao.create(postInput)
  }

  def lookup(id: Int)(
      implicit mc: MarkerContext): Future[Participant] = {
    val k = "Participant::%d".format(id)
    val rv = cache.get(k)
    cache.get(k) match {
      case Some(sp) => {
        decode[Participant](sp.toString) match {
          case Left(d) => {
            logger.warn("cannot parse cache entry: ".concat(d.getLocalizedMessage))
            cache.del(k)
            participantDao.get(id).map(p => {
              cache.put(k, p.asJson.noSpaces)
              p
            })
          }
          case Right(p) => {
            Future.successful(p)
          }
        }
      }
      case None => {
        participantDao.get(id).map(p => {
          cache.put(k, p.asJson.noSpaces)
          p
        })
      }
    }

    
  }

}
