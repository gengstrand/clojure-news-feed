package info.glennengstrand.news.service

import javax.inject.{Inject, Provider}

import play.api.{Logger, MarkerContext}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

import info.glennengstrand.news.model._
import info.glennengstrand.news.dao._
import info.glennengstrand.news.resource._
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object FriendService {
  def key(id: Int): String = "Friends::%d".format(id)
  val logger = Logger(this.getClass)
}

class FriendService @Inject()(
    routerProvider: Provider[ParticipantRouter],
    friendDao: FriendDao, 
    cache: CacheWrapper)(implicit ec: ExecutionContext) {

  def create(id: Int, postInput: Friend)(
      implicit mc: MarkerContext): Future[Friend] = {
    val k = FriendService.key(id)
    cache.del(k)
    friendDao.create(id, postInput)
  }

  def lookup(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Friend]] = {
    val k = FriendService.key(id)
    val rv = cache.get(k)
    cache.get(k) match {
      case Some(sf) => {
        decode[Seq[Friend]](sf.toString) match {
          case Left(d) => {
            FriendService.logger.warn("cannot parse cache entry: ".concat(d.getLocalizedMessage))
            cache.del(k)
            friendDao.get(id).map( f => {
              cache.put(k, f.asJson.noSpaces)
              f
            })
          }
          case Right(f) => {
            Future.successful(f)
          }
        }
      }
      case None => {
        friendDao.get(id).map( f => {
          cache.put(k, f.asJson.noSpaces)
          f
        })
      }
    }
  }

}
