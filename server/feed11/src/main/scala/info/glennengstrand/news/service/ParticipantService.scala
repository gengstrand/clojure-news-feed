package info.glennengstrand.news.service

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Participant
import info.glennengstrand.news.dao.ParticipantDao
import scala.concurrent._
import ExecutionContext.Implicits.global
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object ParticipantService extends NewsFeedService {
  var dao = new ParticipantDao
  var cache: CacheWrapper = RedisCache
  private def load(id: Int, k: String, f: Try[Participant] => Unit): Unit = {
    dao.fetchSingle(id) onComplete {
      case Success(p) => {
        cache.put(k, p.asJson.noSpaces)
        f(Success(p))
      }
      case Failure(e) => f(Failure(e))
    }
  }
  def create(p: Participant, f: Try[Participant] => Unit): Unit = {
    dao.insert(p) onComplete {
      case Success(rp) => f(Success(rp))
      case Failure(e) => f(Failure(e))
    }
  }
  def get(id: Int, f: Try[Participant] => Unit): Unit = {
    val k = "Participant::%d".format(id)
    cache.get(k) match {
      case Some(sp) => {
        decode[Participant](sp.toString) match {
          case Left(d) => {
            LOGGER.warn("cannot parse cache entry: ".concat(d.getLocalizedMessage))
            cache.del(k)
            load(id, k, f)
          }
          case Right(p) => {
            f(Success(p))
          }
        }
      }
      case None => {
        load(id, k, f)
      }
    }
  }
}