package info.glennengstrand.news.service

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Inbound
import info.glennengstrand.news.dao.InboundDao
import scala.concurrent._
import ExecutionContext.Implicits.global

object InboundService extends NewsFeedService {
  var dao = new InboundDao
  def create(ib: Inbound, f: Try[Inbound] => Unit): Unit = {
    dao.insert(ib) onComplete {
      case Success(rv) => {
        f(Success(rv))
      }
      case Failure(e) => {
        LOGGER.error("cannot create inbound: ", e.getLocalizedMessage)
      }
    }
  }
  def get(id: Int, f: Try[Seq[Inbound]] => Unit): Unit = {
    dao.fetchMulti(id) onComplete {
      case Success(rv) => {
        f(Success(rv))
      }
      case Failure(e) => {
        LOGGER.error("cannot fetch inbound: ", e.getLocalizedMessage)
      }
    }
  }
}