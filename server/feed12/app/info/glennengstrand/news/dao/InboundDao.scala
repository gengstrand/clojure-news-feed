package info.glennengstrand.news.dao

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future
import info.glennengstrand.news.model._

class InboundExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait InboundDao {
  def create(id: Int, data: Inbound)(implicit mc: MarkerContext): Future[Inbound]

  def get(id: Int)(implicit mc: MarkerContext): Future[Seq[Inbound]]
}

@Singleton
class InboundDaoImpl @Inject()()(implicit ec: InboundExecutionContext)
    extends InboundDao {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Inbound]] = {
    Future {
      logger.trace(s"get: id = $id")
      Seq(Inbound(None, None, None, None, None))
    }
  }

  def create(id: Int, data: Inbound)(implicit mc: MarkerContext): Future[Inbound] = {
    Future {
      logger.trace(s"create: data = $data")
      data
    }
  }

}

object MockInboundDaoImpl {
  val test = Inbound(
      from = Option("/participant/1"),
      to = Option("/participant/2"),
      occurred = Option("2020/06/14"),
      subject = Option("test subject"),
      story = Option("test story")
  )
}

@Singleton
class MockInboundDaoImpl @Inject()()(implicit ec: InboundExecutionContext)
    extends InboundDao {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Inbound]] = {
    Future {
      logger.trace(s"get: id = $id")
      Seq(MockInboundDaoImpl.test)
    }
  }

  override def create(id: Int, data: Inbound)(implicit mc: MarkerContext): Future[Inbound] = {
    Future {
      logger.trace(s"create: data = $data")
      data
    }
  }

}
