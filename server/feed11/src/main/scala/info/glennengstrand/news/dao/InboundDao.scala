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
    val bs = insertStmt.bind(new java.lang.Integer(extractId(ib.to.get.toString.asInstanceOf[String]).toInt), new java.lang.Integer(extractId(ib.from.get.toString.asInstanceOf[String]).toInt), ib.subject.get.toString.asInstanceOf[String], ib.story.get.toString.asInstanceOf[String])
    CassandraDao.session.execute(bs)
    Future(ib)
  }
  override def fetchMulti(id: Int): Future[Seq[Inbound]] = {
    val bs = selectStmt.bind(id.asInstanceOf[Object])
    val retVal = for {
      r <- CassandraDao.session.execute(bs).iterator().asScala
    } yield Inbound(Option(toLink(r.getInt(1).toLong)), Option(toLink(id)), Option(r.getInstant(0).toString()), Option(r.getString(2)), Option(r.getString(3)))
    Future(retVal.toSeq)
  }
}