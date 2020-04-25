package info.glennengstrand.news.service

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Friend
import info.glennengstrand.news.dao.FriendDao
import scala.concurrent._
import ExecutionContext.Implicits.global
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object FriendService extends NewsFeedService {
  var dao = new FriendDao
  var cache: CacheWrapper = RedisCache
  private def load(id: Int, k: String, f: Try[Seq[Friend]] => Unit): Unit = {
    dao.fetchMulti(id) onComplete {
      case Success(p) => {
        cache.put(k, p.asJson.noSpaces)
        f(Success(p))
      }
      case Failure(e) => f(Failure(e))
    }
  }
  private def key(id: Int): String = {
    "Friends::%d".format(id)
  }
  def create(p: Friend, f: Try[Friend] => Unit): Unit = {
    dao.insert(p) onComplete {
      case Success(rp) => {
        cache.del(key(dao.extractId(rp.from.get).toInt))
        f(Success(rp))
      }
      case Failure(e) => f(Failure(e))
    }
  }
  def get(id: Int, f: Try[Seq[Friend]] => Unit): Unit = {
    val k = key(id)
    cache.get(k) match {
      case Some(sp) => {
        decode[Seq[Friend]](sp.toString) match {
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