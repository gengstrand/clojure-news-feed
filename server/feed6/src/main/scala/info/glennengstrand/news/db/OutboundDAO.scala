package info.glennengstrand.news.db

import info.glennengstrand.news.core.ItemDAO
import info.glennengstrand.news.model.Outbound
import com.datastax.driver.core.{ Session, PreparedStatement, BoundStatement }
import scala.collection.JavaConverters._

object OutboundDAO {
  def apply(db: Session): OutboundDAO = {
    val select = "select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc"
    val upsert = "insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)"
    new OutboundDAO(db.prepare(select), db.prepare(upsert))
  }
}
class OutboundDAO(select: PreparedStatement, upsert: PreparedStatement) extends ItemDAO[Outbound] {
  def gets(id: Long)(implicit db: Session): List[Outbound] = {
    val bs = new BoundStatement(select)
    bs.setInt(0, id.toInt)
    val retVal = for {
      r <- db.execute(bs.bind()).iterator().asScala
      val occurred = r.getDate(0)
      val from = r.getInt(1)
      val subject = r.getString(2)
      val story = r.getString(3)
    } yield Outbound(Option(from.toLong), Option(new java.util.Date(occurred.getMillisSinceEpoch)), Option(subject), Option(story))
    retVal.toList
  }
  def add(item: Outbound)(implicit db: Session): Outbound = {
    val bs = new BoundStatement(upsert)
    val data = for {
      from <- item.from
      subject <- item.subject
      story <- item.story
    } yield (from, subject, story)
    data.foreach(d => {
      bs.setInt(0, d._1.toInt)
      bs.setString(1, d._2)
      bs.setString(2, d._3)
    })
    if (data.size > 0) {
      db.execute(bs.bind())
    }
    item
  }

}

class MockOutboundDAO extends ItemDAO[Outbound] {
  def gets(id: Long)(implicit db: Session): List[Outbound] = {
    List()
  }
  def add(item: Outbound)(implicit db: Session): Outbound = {
    item
  }
}
