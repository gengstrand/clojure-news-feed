package info.glennengstrand.news.dao

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future
import info.glennengstrand.news.model._

class FriendExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait FriendDao {
  def create(id: Int, data: Friend)(implicit mc: MarkerContext): Future[Friend]

  def get(id: Int)(implicit mc: MarkerContext): Future[Seq[Friend]]
}

@Singleton
class FriendDaoImpl @Inject()()(implicit ec: FriendExecutionContext)
    extends FriendDao {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Friend]] = {
    Future {
      logger.trace(s"get: id = $id")
      Seq(Friend(None, None, None))
    }
  }

  override def create(id: Int, data: Friend)(implicit mc: MarkerContext): Future[Friend] = {
    Future {
      logger.trace(s"create: data = $data")
      data
    }
  }

}

object MockFriendDaoImpl {
  val test = Friend(None, Some("/participant/1"), Some("/participant/2"))
}

@Singleton
class MockFriendDaoImpl @Inject()()(implicit ec: FriendExecutionContext)
    extends FriendDao {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Friend]] = {
    Future {
      logger.trace(s"get: id = $id")
      Seq(MockFriendDaoImpl.test)
    }
  }

  def create(id: Int, data: Friend)(implicit mc: MarkerContext): Future[Friend] = {
    Future {
      logger.trace(s"create: data = $data")
      data
    }
  }

}
