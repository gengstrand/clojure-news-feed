package info.glennengstrand.news

import java.util.Date
import java.util.logging.Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.glennengstrand.io._

/** helper functions for outbound object creation */
object Outbound extends SolrSearcher {
  val log = LoggerFactory.getLogger("info.glennengstrand.news.Outbound")
  val log2 = java.util.logging.Logger.getLogger("info.glennengstrand.news.Outbound")
val reader: PersistentDataStoreReader = new CassandraReader
  val cache: CacheAware = new MockCache
  class OutboundBindings extends PersistentDataStoreBindings {
    def entity: String = {
      "Outbound"
    }
    def fetchInputs: Iterable[String] = {
      List("participantID")
    }
    def fetchOutputs: Iterable[(String, String)] = {
      List(("occurred", "Date"), ("subject", "String"), ("story", "String"))
    }
    def upsertInputs: Iterable[String] = {
      List("participantID", "occurred", "subject", "story")
    }
    def upsertOutputs: Iterable[(String, String)] = {
      List()
    }
    def fetchOrder: Map[String, String] = {
      Map("occurred" -> "desc")
    }
  }
  val bindings = new OutboundBindings
  def apply(id: Int) : OutboundFeed = {
    val criteria: Map[String, Any] = Map("participantID" -> id)
    new OutboundFeed(id, IO.cacheAwareRead(bindings, criteria, reader, cache)) with CassandraWriter with MockCacheAware with SolrSearcher
  }
  def apply(state: String): Outbound = {
    val s = IO.fromFormPost(state)
    val id = s("from").asInstanceOf[String].toInt
    val story = s("story").asInstanceOf[String]
    index(id, story)
    new Outbound(id, IO.convertToDate(s("occurred").asInstanceOf[String]), s("subject").asInstanceOf[String], story) with CassandraWriter with MockCacheAware
  }
  def lookup(state: String): Iterable[OutboundFeed] = {
    val s = IO.fromFormPost(state)
    val terms = s("terms").asInstanceOf[String]
    search(terms).map(id => Outbound(id.toInt))
  }
}

case class OutboundState(participantID: Int, occurred: Date, subject: String, story: String)

/** represents a news feed item in the outbound feed */
class Outbound(participantID: Int, occurred: Date, subject: String, story: String) extends OutboundState(participantID, occurred, subject, story) with MicroServiceSerializable {
  this: PersistentDataStoreWriter with CacheAware =>

  def getState: Map[String, Any] = {
    Map(
      "participantID" -> participantID,
      "occurred" -> occurred,
      "subject" -> subject,
      "story" -> story
    )
  }

  /** save item to db and perform social broadcast of item to inbound feed of friends */
  def save: Unit = {
    val criteria: Map[String, Any] = Map(
      "participantID" -> participantID
    )
    write(Outbound.bindings, getState, criteria)
    invalidate(Outbound.bindings, criteria)
    val broadcast = Friends(participantID)
    // fetch friends returns zero results
    broadcast.foreach( f => {
      Outbound.log2.finest(s"creating new inbound for ${f.toParticipantID}")
      val inbound =  new Inbound(f.toParticipantID.toInt, occurred, participantID, subject, story) with CassandraWriter with MockCacheAware
      inbound.save
    })
  }
  override def toJson: String = {
    IO.toJson(getState)
  }
  override def toJson(factory: FactoryClass): String = toJson
}

/** represents a user's outbound feed */
class OutboundFeed(id: Int, state: Iterable[Map[String, Any]]) extends Iterator[Outbound] with MicroServiceSerializable {
  val i = state.iterator
  def hasNext = i.hasNext
  def next() = {
    val kv = i.next()
    Outbound.log.debug("kv = " + kv)
    val occurred = kv.contains("dateOf(occurred)") match {
      case true => kv("dateOf(occurred)")
      case _ => kv("occurred")
    }
    new Outbound(id, IO.convertToDate(occurred), kv("subject").toString, kv("story").toString) with CassandraWriter with MockCacheAware with SolrSearcher
  }
  override def toJson: String = {
    "[" +  map(f => f.toJson).reduce(_ + "," + _) + "]"
  }
  override def toJson(factory: FactoryClass): String = toJson
}


