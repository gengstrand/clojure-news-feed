package info.glennengstrand.news

import java.util.Date
import java.util.logging.Logger

import info.glennengstrand.io._

object Inbound {
  val log = Logger.getLogger("info.glennengstrand.news.Inbound")
  val reader: PersistentDataStoreReader = new CassandraReader
  val cache: CacheAware = new MockCache
  class InboundBindings extends PersistentDataStoreBindings {
    def entity: String = {
      "Inbound"
    }
    def fetchInputs: Iterable[String] = {
      List("participantID")
    }
    def fetchOutputs: Iterable[(String, String)] = {
      List(("dateOf(occurred)", "Date"), ("fromParticipantID", "Int"), ("subject", "String"), ("story", "String"))
    }
    def upsertInputs: Iterable[String] = {
      List("participantID", "fromParticipantID", "occurred", "subject", "story")
    }
    def upsertOutputs: Iterable[(String, String)] = {
      List()
    }
    def fetchOrder: Map[String, String] = {
      Map("occurred" -> "desc")
    }
  }
  val bindings = new InboundBindings
  def apply(id: Int) : InboundFeed = {
    val criteria: Map[String, Any] = Map("participantID" -> id)
    new InboundFeed(id, IO.cacheAwareRead(bindings, criteria, reader, cache)) with CassandraWriter with MockCacheAware
  }
  def apply(state: String): Inbound = {
    val s = IO.fromFormPost(state)
    new Inbound(s("participantID").asInstanceOf[String].toLong, IO.df.parse(s("occurred").asInstanceOf[String]), s("fromParticipantID").asInstanceOf[String].toLong, s("subject").asInstanceOf[String], s("story").asInstanceOf[String]) with CassandraWriter with MockCacheAware
  }
}

case class InboundState(participantID: Long, occurred: Date, fromParticipantID: Long, subject: String, story: String)

class Inbound(participantID: Long, occurred: Date, fromParticipantID: Long, subject: String, story: String) extends InboundState(participantID, occurred, fromParticipantID, subject, story) {
  this: PersistentDataStoreWriter with CacheAware =>

  def getState: Map[String, Any] = {
    Map(
      "participantID" -> participantID,
      "occurred" -> occurred,
      "fromParticipantID" -> fromParticipantID,
      "subject" -> subject,
      "story" -> story
    )
  }
  def save: Unit = {
    val criteria: Map[String, Any] = Map(
      "participantID" -> participantID
    )
    write(Inbound.bindings, getState, criteria)
    invalidate(Inbound.bindings, criteria)
  }

  def toJson: String = {
    IO.toJson(getState)
  }

}

class InboundFeed(id: Int, state: Iterable[Map[String, Any]]) extends Iterator[Inbound] {
  val i = state.iterator
  def hasNext = i.hasNext
  def next() = {
    val kv = i.next()
    Inbound.log.finest("kv = " + kv)
    val occurred = kv.contains("dateOf(occurred)") match {
      case true => kv("dateOf(occurred)")
      case _ => kv("occurred")
    }
    new Inbound(id, IO.convertToDate(occurred), IO.convertToLong(kv("fromParticipantID")), kv("subject").toString, kv("story").toString) with CassandraWriter with MockCacheAware
  }
  def toJson: String = {
    "[" +  map(f => f.toJson).reduce(_ + "," + _) + "]"
  }
}

