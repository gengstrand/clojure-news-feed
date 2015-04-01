package info.glennengstrand.news

import java.util.Date
import java.util.logging.Logger

import info.glennengstrand.io._

object Outbound extends SolrSearcher {
  val log = Logger.getLogger("info.glennengstrand.news.Outbound")
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
      List(("dateOf(occurred)", "Date"), ("subject", "String"), ("story", "String"))
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
    val id = s("participantID").asInstanceOf[String].toLong
    val story = s("story").asInstanceOf[String]
    index(id, story)
    new Outbound(id, IO.df.parse(s("occurred").asInstanceOf[String]), s("subject").asInstanceOf[String], story) with CassandraWriter with MockCacheAware
  }
  def lookup(state: String): Iterable[OutboundFeed] = {
    val s = IO.fromFormPost(state)
    val terms = s("terms").asInstanceOf[String]
    search(terms).map(id => Outbound(id.toInt))
  }
}

case class OutboundState(participantID: Long, occurred: Date, subject: String, story: String)

class Outbound(participantID: Long, occurred: Date, subject: String, story: String) extends OutboundState(participantID, occurred, subject, story) {
  this: PersistentDataStoreWriter with CacheAware =>

  def getState: Map[String, Any] = {
    Map(
      "participantID" -> participantID,
      "occurred" -> occurred,
      "subject" -> subject,
      "story" -> story
    )
  }
  def save: Unit = {
    val criteria: Map[String, Any] = Map(
      "participantID" -> participantID
    )
    write(Outbound.bindings, getState, criteria)
    invalidate(Outbound.bindings, criteria)
  }
  def toJson: String = {
    IO.toJson(getState)
  }

}

class OutboundFeed(id: Int, state: Iterable[Map[String, Any]]) extends Iterator[Outbound] {
  val i = state.iterator
  def hasNext = i.hasNext
  def next() = {
    val kv = i.next()
    Outbound.log.finest("kv = " + kv)
    val occurred = kv.contains("dateOf(occurred)") match {
      case true => kv("dateOf(occurred)")
      case _ => kv("occurred")
    }
    new Outbound(id, IO.convertToDate(occurred), kv("subject").toString, kv("story").toString) with CassandraWriter with MockCacheAware with SolrSearcher
  }
  def toJson: String = {
    "[" +  map(f => f.toJson).reduce(_ + "," + _) + "]"
  }
}


