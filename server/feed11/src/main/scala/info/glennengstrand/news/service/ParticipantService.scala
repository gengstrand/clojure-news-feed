package info.glennengstrand.news.service

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Participant
import info.glennengstrand.news.dao.ParticipantDao
import scala.concurrent._
import ExecutionContext.Implicits.global

object ParticipantService extends NewsFeedService {
  var dao = new ParticipantDao
  def create(p: Participant, f: Try[Participant] => Unit): Unit = {
    dao.insert(p) onComplete {
      case Success(rp) => f(Success(rp))
      case Failure(e) => f(Failure(e))
    }
  }
  def get(id: Int, f: Try[Participant] => Unit): Unit = {
    dao.fetchSingle(id) onComplete {
      case Success(p) => f(Success(p))
      case Failure(e) => f(Failure(e))
    }
  }
}