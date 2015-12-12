package info.glennengstrand.news

import java.sql.PreparedStatement

import info.glennengstrand.io._
import com.google.inject.Stage
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import com.twitter.finagle.http.Status.Ok

trait MockWriter extends PersistentDataStoreWriter {
  def write(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Map[String, Any] = {
    Map("id" -> 1l)
  }
}

trait MockRelationalWriter extends PersistentRelationalDataStoreWriter {
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    null
  }
}

trait MockSearcher extends PersistentDataStoreSearcher {
  def search(terms: String): Iterable[java.lang.Long] = {
    List(1l, 2l, 3l)
  }
  def index(id: Long, content: String): Unit = {

  }

}

class MockPerformanceLogger extends PerformanceLogger {
  def log(topic: String, entity: String, operation: String, duration: Long): Unit = {

  }
}

class MockFactoryClass extends FactoryClass {

  val performanceLogger = new MockPerformanceLogger
  def isEmpty: Boolean = false
  def getParticipant(id: Long): Participant = {
    new Participant(id, "test") with MockRelationalWriter with MockCacheAware
  }
  def getParticipant(state: String): Participant = {
    val s = IO.fromFormPost(state)
    new Participant(s("id").asInstanceOf[String].toLong, s("name").asInstanceOf[String]) with MockRelationalWriter with MockCacheAware
  }
  def getFriends(id: Long): Friends = {
    val state: Iterable[Map[String, Any]] = List(
      Map[String, Any](
      "FriendsID" -> "1",
      "ParticipantID" -> "2"
      )
    )
    new Friends(1l, state)
  }
  def getFriend(state: String): Friend = {
    val s = IO.fromFormPost(state)
    new Friend(s("FriendsID").asInstanceOf[String].toLong, s("FromParticipantID").asInstanceOf[String].toInt, s("ToParticipantID").asInstanceOf[String].toInt) with MockRelationalWriter with MockCacheAware
  }
  def getInbound(id: Int): InboundFeed = {
    val state: Iterable[Map[String, Any]] = List(
      Map[String, Any](
        "participantID" -> "1",
        "occurred" -> "2014-01-12 22:56:36-0800",
        "fromParticipantID" -> "2",
        "subject" -> "test",
        "story" -> "this is a unit test"
      )
    )
    new InboundFeed(1, state)
  }
  def getInbound(state: String): Inbound = {
    val s = IO.fromFormPost(state)
    new Inbound(s("participantID").asInstanceOf[String].toInt, IO.df.parse(s("occurred").asInstanceOf[String]), s("fromParticipantID").asInstanceOf[String].toInt, s("subject").asInstanceOf[String], s("story").asInstanceOf[String]) with MockWriter with MockCacheAware
  }
  def getOutbound(id: Int): OutboundFeed = {
    val state: Iterable[Map[String, Any]] = List(
      Map[String, Any](
        "participantID" -> "1",
        "occurred" -> "2014-01-12 22:56:36-0800",
        "fromParticipantID" -> "2",
        "subject" -> "test",
        "story" -> "this is a unit test"
      )
    )
    new OutboundFeed(1, state)
  }
  def getOutbound(state: String): Outbound = {
    val s = IO.fromFormPost(state)
    new Outbound(s("participantID").asInstanceOf[String].toInt, IO.df.parse(s("occurred").asInstanceOf[String]), s("subject").asInstanceOf[String], s("story").asInstanceOf[String]) with MockWriter with MockCacheAware
  }
  def getObject(name: String, id: Long): Option[Object] = {
    name match {
      case "participant" => Some(getParticipant(id))
      case "friends" => Some(getFriends(id))
      case _ => None
    }
  }
  def getObject(name: String, id: Int): Option[Object] = {
    name match {
      case "inbound" => Some(getInbound(id))
      case "outbound" => Some(getOutbound(id))
      case _ => None
    }
  }
  def getObject(name: String, state: String): Option[Object] = {
    name match {
      case "participant" => Some(getParticipant(state))
      case "friend" => Some(getFriend(state))
      case "inbound" => Some(getInbound(state))
      case "outbound" => Some(getOutbound(state))
      case _ => None
    }
  }
  def getObject(name: String): Option[Object] = {
    name match {
      case "logger" => Some(performanceLogger)
      case _ => None
    }
  }
}

/** unit tests for the news feed service */
class FeedSpec extends FeatureTest {
  Feed.factory = new MockFactoryClass
  IO.cacheStatements = false
  IO.unitTesting = true

  override val server = new EmbeddedHttpServer(new NewsFeedServer)

  "Server" should {
    "startup" in {
      server.assertHealthy()
    }

    "return the correct data when fetching a participant" in {
      server.httpGet(path = "/participant/2", andExpect = Ok)
    }

    "return the correct data when fetching friends" in {
      server.httpGet(path = "/friends/1", andExpect = Ok)
    }

    "return the correct data when fetching inbound" in {
      server.httpGet(path = "/inbound/1", andExpect = Ok)
    }

    "process post requests to create a new participant properly" in {
      server.httpPost(path = "/participant/new", postBody = "id=2&name=smith", andExpect = Ok)
    }
  }

}
