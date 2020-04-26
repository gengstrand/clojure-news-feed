package info.glennengstrand.news.dao

import scala.concurrent.Future
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection.JavaConverters._

import com.datastax.oss.driver.api.core.cql.{BoundStatement, PreparedStatement}
import info.glennengstrand.news.model.Inbound

class InboundDao extends DataAccess[Inbound] {
  private val selectCql = "select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc"
  private val insertCql = "insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?)"
  private lazy val insertStmt = CassandraDao.session.prepare(insertCql)
  private lazy val selectStmt = CassandraDao.session.prepare(selectCql)
  
  override def fetchSingle(id: Int): Future[Inbound] = {
    Future(Inbound(None, None, None, None, None))
  }
  override def insert(ib: Inbound): Future[Inbound] = {
    val bs = insertStmt.bind(ib.to.get, ib.from.get, ib.subject.get, ib.story.get)
    CassandraDao.session.execute(bs)
    Future(ib)
  }
  override def fetchMulti(id: Int): Future[Seq[Inbound]] = {
    val bs = selectStmt.bind(id.asInstanceOf[Object])
    val retVal = for {
      r <- CassandraDao.session.execute(bs).iterator().asScala
      val occurred = r.getLocalTime(0)
      val from = r.getInt(1)
      val subject = r.getString(2)
      val story = r.getString(3)
    } yield Inbound(Option(toLink(from.toLong)), Option(toLink(id)), Option(occurred.toString()), Option(subject), Option(story))
    Future(retVal.toSeq)
  }
}