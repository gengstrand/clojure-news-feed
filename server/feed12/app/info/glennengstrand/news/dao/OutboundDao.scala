package info.glennengstrand.news.dao

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future
import info.glennengstrand.news.model._

class OutboundExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait OutboundDao {
  def create(id: Int, data: Outbound)(implicit mc: MarkerContext): Future[Outbound]

  def get(id: Int)(implicit mc: MarkerContext): Future[Seq[Outbound]]
  
  def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]]
  
}

@Singleton
class OutboundDaoImpl @Inject()(searchDao: SearchDao)(implicit ec: OutboundExecutionContext)
    extends OutboundDao {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Outbound]] = {
    Future {
      logger.trace(s"get: id = $id")
      Seq(Outbound(None, None, None, None))
    }
  }

  override def create(id: Int, data: Outbound)(implicit mc: MarkerContext): Future[Outbound] = {
    Future {
      logger.trace(s"create: data = $data")
      searchDao.index(data.source)
      data
    }
  }
  
  override def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]] = {
    searchDao.search(keywords)
  }

}

object MockOutboundDaoImpl {
  val test = Outbound(
      from = Option("/participant/1"),
      occurred = Option("2020/06/14"),
      subject = Option("test subject"),
      story = Option("test story")
  )
}

@Singleton
class MockOutboundDaoImpl @Inject()()(implicit ec: OutboundExecutionContext)
    extends OutboundDao {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Outbound]] = {
    Future {
      logger.trace(s"get: id = $id")
      Seq(MockOutboundDaoImpl.test)
    }
  }

  override def create(id: Int, data: Outbound)(implicit mc: MarkerContext): Future[Outbound] = {
    Future {
      logger.trace(s"create: data = $data")
      data
    }
  }
  
  override def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]] = {
    Future {
      Seq("/participant/1")
    }
  }

}
