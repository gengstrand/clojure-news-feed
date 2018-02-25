package info.glennengstrand.news.db

import info.glennengstrand.news.core.{ ItemDAO, DocumentIdentity }
import info.glennengstrand.news.model.Outbound
import com.datastax.driver.core.{ Session, PreparedStatement, BoundStatement }
import scala.collection.JavaConverters._
import org.elasticsearch.client.RestHighLevelClient
import java.util.UUID
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._

object OutboundItemDAO {
  def apply(db: Session): OutboundItemDAO = {
    val select = "select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc"
    val upsert = "insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)"
    new OutboundItemDAO(db.prepare(select), db.prepare(upsert))
  }
}
class OutboundItemDAO(select: PreparedStatement, upsert: PreparedStatement) extends ItemDAO[Outbound] {

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

class MockOutboundItemDAO extends ItemDAO[Outbound] {

  def gets(id: Long)(implicit db: Session): List[Outbound] = {
    List()
  }
  def add(item: Outbound)(implicit db: Session): Outbound = {
    item
  }
}

class OutboundDocumentDAO(es: RestHighLevelClient) extends ElasticSearchDAO[Outbound] {
  def client: RestHighLevelClient = {
    es
  }
  def identity: DocumentIdentity = {
    DocumentIdentity("feed", "stories", UUID.randomUUID().toString(), "story", "sender")
  }
  override def source(doc: Outbound, key: String): String = {
    val retVal = ("id" -> key) ~ ("sender" -> doc.from) ~ ("story" -> doc.story)
    compact(render(retVal))
  }
}

class MockOutboundDocumentDAO extends ElasticSearchDAO[Outbound] {
  def client: RestHighLevelClient = {
    null
  }
  def identity: DocumentIdentity = {
    DocumentIdentity("feed", "stories", "test", "story", "sender")
  }
  def source(doc: Outbound): String = {
    ""
  }
  override def index(doc: Outbound): Unit = {

  }
  override def search(keywords: String): List[Int] = {
    List()
  }
}

