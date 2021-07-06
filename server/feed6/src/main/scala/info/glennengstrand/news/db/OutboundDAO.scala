package info.glennengstrand.news.db

import info.glennengstrand.news.core.{ ItemDAO, DocumentIdentity }
import info.glennengstrand.news.model.Outbound
import info.glennengstrand.news.Link
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
      val occurred = r.getTimestamp(0)
      val from = id.toInt
      val subject = r.getString(1)
      val story = r.getString(2)
    } yield Outbound(Option(Link.toLink(from.toLong)), Option(occurred), Option(subject), Option(story))
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
      bs.setInt(0, Link.extractId(d._1).toInt)
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
  override def source(doc: Outbound, key: String): Map[String, Object] = {
    val retVal = for {
      from <- doc.from
      story <- doc.story
    } yield Map("id" -> key, "sender" -> from, "story" -> story)
    if (retVal.size > 0) {
      retVal.head.asInstanceOf[Map[String, Object]]
    } else {
      Map()
    }
  }
}

class MockOutboundDocumentDAO extends ElasticSearchDAO[Outbound] {
  def client: RestHighLevelClient = {
    null
  }
  def identity: DocumentIdentity = {
    DocumentIdentity("feed", "stories", "test", "story", "sender")
  }
  def source(doc: Outbound): Map[String, Object] = {
    Map()
  }
  override def index(doc: Outbound): Unit = {

  }
  override def search(keywords: String): List[Int] = {
    List()
  }
}

