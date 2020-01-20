package info.glennengstrand.news.db

import info.glennengstrand.news.core.{ ItemDAO, DocumentDAO, DocumentIdentity }
import info.glennengstrand.news.model.Inbound
import info.glennengstrand.news.Link
import com.datastax.driver.core.{ Session, PreparedStatement, BoundStatement }
import scala.collection.JavaConverters._

object InboundDAO {
  def apply(db: Session): InboundDAO = {
    val select = "select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc"
    val upsert = "insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?)"
    new InboundDAO(db.prepare(select), db.prepare(upsert))
  }
}
class InboundDAO(select: PreparedStatement, upsert: PreparedStatement) extends ItemDAO[Inbound] {
  def gets(id: Long)(implicit db: Session): List[Inbound] = {
    val bs = new BoundStatement(select)
    bs.setInt(0, id.toInt)
    val retVal = for {
      r <- db.execute(bs.bind()).iterator().asScala
      val occurred = r.getTimestamp(0)
      val from = r.getInt(1)
      val subject = r.getString(2)
      val story = r.getString(3)
    } yield Inbound(Option(Link.toLink(from.toLong)), Option(Link.toLink(id)), Option(occurred), Option(subject), Option(story))
    retVal.toList
  }
  def add(item: Inbound)(implicit db: Session): Inbound = {
    val bs = new BoundStatement(upsert)
    val data = for {
      to <- item.to
      from <- item.from
      subject <- item.subject
      story <- item.story
    } yield (to, from, subject, story)
    data.foreach(d => {
      bs.setInt(0, Link.extractId(d._1).toInt)
      bs.setInt(1, Link.extractId(d._2).toInt)
      bs.setString(2, d._3)
      bs.setString(3, d._4)
    })
    if (data.size > 0) {
      db.execute(bs.bind())
    }
    item
  }
}
class MockInboundDAO extends ItemDAO[Inbound] {
  def gets(id: Long)(implicit db: Session): List[Inbound] = {
    List()
  }
  def add(item: Inbound)(implicit db: Session): Inbound = {
    item
  }
}
class MockInboundDocumentDAO extends DocumentDAO[Inbound] {

  def identity: DocumentIdentity = {
    null
  }
  override def index(doc: Inbound): Unit = {

  }
  def search(keywords: String): List[Int] = {
    List()
  }
}
