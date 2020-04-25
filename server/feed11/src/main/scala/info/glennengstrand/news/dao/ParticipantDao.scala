package info.glennengstrand.news.dao

import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future
import info.glennengstrand.news.model.Participant

class ParticipantDao extends DataAccess[Participant] {
  override def fetchSingle(id: Int): Future[Participant] = {
    val s = sql"call FetchParticipant($id)".as[String]
    MySqlDao.db.run(s.map(r => Participant(Option(id), Option(r(0)), Option(toLink(id)))))
  }
  override def insert(p: Participant): Future[Participant] = {
    val s = sql"call UpsertParticipant(${p.name.get})".as[Int]
    MySqlDao.db.run(s.map(r => Participant(Option(r(0)), p.name, Option(toLink(r(0))))))
  }
}