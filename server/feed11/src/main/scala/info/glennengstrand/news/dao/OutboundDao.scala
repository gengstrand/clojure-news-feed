package info.glennengstrand.news.dao

import scala.concurrent.Future
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection.JavaConverters._

import com.datastax.oss.driver.api.core.cql.{BoundStatement, PreparedStatement}
import info.glennengstrand.news.model.Outbound

class OutboundDao extends DataAccess[Outbound] {
  private val selectCql = "select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc"
  private val insertCql = "insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)"
  private lazy val insertStmt = CassandraDao.session.prepare(insertCql)
  private lazy val selectStmt = CassandraDao.session.prepare(selectCql)
  
  override def fetchSingle(id: Int): Future[Outbound] = {
    Future(Outbound(None, None, None, None))
  }
  override def insert(ob: Outbound): Future[Outbound] = {
    val bs = insertStmt.bind(ob.from.get, ob.subject.get, ob.story.get)
    CassandraDao.session.execute(bs)
    Future(ob)
  }
  override def fetchMulti(id: Int): Future[Seq[Outbound]] = {
    val bs = selectStmt.bind(id.asInstanceOf[Object])
    val retVal = for {
      r <- CassandraDao.session.execute(bs).iterator().asScala
    } yield Outbound(Option(toLink(r.getInt(1).toLong)), Option(r.getLocalTime(0).toString()), Option(r.getString(2)), Option(r.getString(3)))
    Future(retVal.toSeq)
  }
}