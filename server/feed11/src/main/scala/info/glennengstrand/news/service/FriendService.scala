package info.glennengstrand.news.service

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Friend
import info.glennengstrand.news.dao.FriendDao
import scala.concurrent._
import ExecutionContext.Implicits.global

object FriendService extends NewsFeedService {
  var dao = new FriendDao
  def create(p: Friend, f: Try[Friend] => Unit): Unit = {
    dao.insert(p) onComplete {
      case Success(rp) => f(Success(rp))
      case Failure(e) => f(Failure(e))
    }
  }
  def get(id: Int, f: Try[Seq[Friend]] => Unit): Unit = {
    dao.fetchMulti(id) onComplete {
      case Success(p) => f(Success(p))
      case Failure(e) => f(Failure(e))
    }
  }
}