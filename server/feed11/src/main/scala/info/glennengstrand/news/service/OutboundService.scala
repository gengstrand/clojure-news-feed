package info.glennengstrand.news.service

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Outbound
import info.glennengstrand.news.dao.{OutboundDao, ElasticSearchDao}
import scala.concurrent._
import ExecutionContext.Implicits.global

object OutboundService extends NewsFeedService {
  var dao = new OutboundDao
  var search = new ElasticSearchDao
  def create(ob: Outbound, f: Try[Outbound] => Unit): Unit = {
    dao.insert(ob) onComplete {
      case Success(rv) => {
        search.index(Map("sender" -> dao.extractId(ob.from.get).asInstanceOf[Object], "story" -> ob.story.get))
        f(Success(rv))
      }
      case Failure(e) => {
        LOGGER.error("cannot create outbound: ", e.getLocalizedMessage)
        f(Failure(e))
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
        f(Failure(e))
      }
    }
  }
  def search(keywords: String, f: Try[Seq[String]] => Unit): Unit = {
    f(Success(search.search(keywords).map(r => dao.toLink(r))))
  }
}