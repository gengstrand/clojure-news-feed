package info.glennengstrand.news.dao

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.Future
import slick.jdbc.MySQLProfile.api._
import info.glennengstrand.news.model._

class ParticipantExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait ParticipantDao extends Link {
  def create(data: Participant)(implicit mc: MarkerContext): Future[Participant]

  def get(id: Int)(implicit mc: MarkerContext): Future[Participant]
}

@Singleton
class ParticipantDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ParticipantExecutionContext)
    extends ParticipantDao with HasDatabaseConfigProvider[JdbcProfile] {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
    implicit mc: MarkerContext): Future[Participant] = {
    val s = sql"call FetchParticipant($id)".as[String]
    dbConfig.db.run(s.map(r => {
      r.isEmpty match {
        case true => Participant(None, None, None)
        case false => Participant(Option(id.toLong), Option(r(0)), Option(toLink(id.toLong)))
      }
    }))
  }

  override def create(data: Participant)(implicit mc: MarkerContext): Future[Participant] = {
    val s = sql"call UpsertParticipant(${data.name.get})".as[Int]
    dbConfig.db.run(s.map(r => {
      r.isEmpty match {
        case true => Participant(None, None, None)
        case false => Participant(Option(r(0).toLong), data.name, Option(toLink(r(0).toLong)))
      }
    }))
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
