package info.glennengstrand.news.dao

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future
import info.glennengstrand.news.model._

class ParticipantExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait ParticipantDao {
  def create(data: Participant)(implicit mc: MarkerContext): Future[Participant]

  def get(id: Int)(implicit mc: MarkerContext): Future[Participant]
}

@Singleton
class ParticipantDaoImpl @Inject()()(implicit ec: ParticipantExecutionContext)
    extends ParticipantDao {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Participant] = {
    Future {
      logger.trace(s"get: id = $id")
      Participant(None, None, None)
    }
  }

  override def create(data: Participant)(implicit mc: MarkerContext): Future[Participant] = {
    Future {
      logger.trace(s"create: data = $data")
      data
    }
  }

}

object MockParticipantDaoImpl {
  val test = Participant(None, Some("test"), None)
}

@Singleton
class MockParticipantDaoImpl @Inject()()(implicit ec: ParticipantExecutionContext)
    extends ParticipantDao {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Participant] = {
    Future {
      logger.trace(s"get: id = $id")
      MockParticipantDaoImpl.test
    }
  }

  def create(data: Participant)(implicit mc: MarkerContext): Future[Participant] = {
    Future {
      logger.trace(s"create: data = $data")
      data
    }
  }

}
