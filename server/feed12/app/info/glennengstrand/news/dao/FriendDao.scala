package info.glennengstrand.news.dao

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future
import info.glennengstrand.news.model._

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

class FriendExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait FriendDao extends Link {
  def create(id: Int, data: Friend)(implicit mc: MarkerContext): Future[Friend]

  def get(id: Int)(implicit mc: MarkerContext): Future[Seq[Friend]]
}

@Singleton
class FriendDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: FriendExecutionContext)
    extends FriendDao with HasDatabaseConfigProvider[JdbcProfile] {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Friend]] = {
    val s = sql"call FetchFriends($id)".as[(Long, Long)]
    dbConfig.db.run(s.map(results => {
      for {
        friend <- results
      } yield Friend(Option(friend._1), Option(toLink(id.toLong)), Option(toLink(friend._2)))
    }))
  }

  override def create(id: Int, data: Friend)(implicit mc: MarkerContext): Future[Friend] = {
    val s = sql"call UpsertFriends(${extractId(data.from.get)}, ${extractId(data.to.get)})".as[Int]
    dbConfig.db.run(s.map(r => {
      r.isEmpty match {
        case true => Friend(None, None, None)
        case false => Friend(Option(r(0).toLong), data.from, data.to)
      }
    }))
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
