package info.glennengstrand.news.dao

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future
import info.glennengstrand.news.model._

class SearchExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait SearchDao {
  def index(doc: Map[String, Object])(implicit mc: MarkerContext): Unit

  def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]]
}

@Singleton
class SearchDaoImpl @Inject()()(implicit ec: SearchExecutionContext)
    extends SearchDao {

  private val logger = Logger(this.getClass)

  override def index(doc: Map[String, Object]) (implicit mc: MarkerContext): Unit = {
      logger.trace(s"index: doc = $doc")
  }

  override def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]] = {
    Future {
      logger.trace(s"search: keywords = $keywords")
      Seq()
    }
  }

}

object MockSearchDaoImpl {
  val test = Seq("/participant/1")
}

@Singleton
class MockSearchDaoImpl @Inject()()(implicit ec: SearchExecutionContext)
    extends SearchDao {

  private val logger = Logger(this.getClass)

  override def index(doc: Map[String, Object]) (implicit mc: MarkerContext): Unit = {
      logger.trace(s"index: doc = $doc")
  }

  override def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]] = {
    Future {
      logger.trace(s"search: keywords = $keywords")
      MockSearchDaoImpl.test
    }
  }

}
