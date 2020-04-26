package info.glennengstrand.news.service

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Outbound
import info.glennengstrand.news.dao.OutboundDao
import scala.concurrent._
import ExecutionContext.Implicits.global

object OutboundService extends NewsFeedService {
  var dao = new OutboundDao
  def create(ob: Outbound, f: Try[Outbound] => Unit): Unit = {
    dao.insert(ob) onComplete {
      case Success(rv) => {
        f(Success(rv))
      }
      case Failure(e) => {
        LOGGER.error("cannot create outbound: ", e.getLocalizedMessage)
      }
    }
  }
  def get(id: Int, f: Try[Seq[Outbound]] => Unit): Unit = {
    dao.fetchMulti(id) onComplete {
      case Success(rv) => {
        f(Success(rv))
      }
      case Failure(e) => {
        LOGGER.error("cannot fetch outbound: ", e.getLocalizedMessage)
      }
    }
  }
  def search(keywords: String, f: Try[Seq[String]] => Unit): Unit = {
    dao.search(keywords) onComplete {
      case Success(rv) => {
        f(Success(rv))
      }
      case Failure(e) => {
        LOGGER.error("cannot search outbound: ", e.getLocalizedMessage)
      }
    }
  }
}