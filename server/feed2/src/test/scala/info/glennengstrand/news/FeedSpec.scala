package info.glennengstrand.news

import java.sql.PreparedStatement

import info.glennengstrand.io._
import com.google.inject.Stage
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import com.twitter.finagle.http.Status.Ok
import org.scalatest.FunSuite

trait MockWriter extends PersistentDataStoreWriter {
  def write(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Map[String, Any] = {
    Map("id" -> 1l)
  }
}

trait MockRelationalWriter extends TransientRelationalDataStoreWriter {
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    null
  }
}

trait MockSearcher extends PersistentDataStoreSearcher {
  def search(terms: String): Iterable[Long] = {
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
  def getParticipant(id: Int): Participant = {
    new Participant(id, "test") with MockRelationalWriter with MockCacheAware
  }
  def getParticipant(state: String): Participant = {
    val s = IO.fromFormPost(state)
    new Participant(s("id").asInstanceOf[String].toInt, s("name").asInstanceOf[String]) with MockRelationalWriter with MockCacheAware
  }
  def getFriends(id: Long): Friends = {
    val state: Iterable[Map[String, Any]] = List(
      Map[String, Any](
      "FriendsID" -> "1",
      "ParticipantID" -> "2"
      )
    )
    new Friends(1, state)
  }
  def getFriend(state: String): Friend = {
    val s = IO.fromFormPost(state)
    new Friend(s("FriendsID").asInstanceOf[String].toInt, s("FromParticipantID").asInstanceOf[String].toInt, s("ToParticipantID").asInstanceOf[String].toInt) with MockRelationalWriter with MockCacheAware
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
  def getObject(name: String, id: Int): Option[Object] = {
    name match {
      case "participant" => Some(getParticipant(id))
      case "friends" => Some(getFriends(id))
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

object TestElastic {
  val json = "{\"took\":45,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,\"failed\":0},\"hits\":{\"total\":2,\"max_score\":0.5538906,\"hits\":[{\"_index\":\"feed\",\"_type\":\"stories\",\"_id\":\"4b76019e-a2f6-4832-9131-35723b6504d9\",\"_score\":0.5538906,\"_source\":{\"id\":\"4b76019e-a2f6-4832-9131-35723b6504d9\",\"sender\":9245,\"story\":\"5084 27908 22992 12038 11379 31565 15961 34623 39056 33471 13720 16101 27237 21555 22581 9591 17529 4383 7376 11299 32618 3152 1804 11274 27128 6316 31904 38656 1077 38933 15345 1651 23795 426 7390 39700 4941 27310 23785 3771 9330 1516 21522 453 23530 10322 20295 36476 24767 30485 680 2549 16424 17026 39440 18831 19501 6212 18056 9141 5665 32659 7471 36920 25056 12029 10219 23252 829 13717 10511 15667 32420 21945 3522 26227 10843 39112 7035 8331 21835 21776 1761 33937 36480 17562 21065 6340 3965 30334 16500 18405 27421 21369 22465 22110 21334 15834 11449 12487 37798 6744 30765 7518 28944 32673 22323 5293 24119 10831 28904 20672 22992 33742 3577 5977 19492 5592 3300 23644 36569 23423 3418 23573 28034 38706 36669 23815 31268 26424 38516 520 33403 21635 29970 1501 3596 37620 9804 27541 5317 15021 39915 36495 35101 8042 10788 17521 38012 31562 \"}},{\"_index\":\"feed\",\"_type\":\"stories\",\"_id\":\"6b589f73-f05a-41e7-8bbc-ba9913f08258\",\"_score\":0.5538906,\"_source\":{\"id\":\"6b589f73-f05a-41e7-8bbc-ba9913f08258\",\"sender\":9461,\"story\":\"21872 32028 20610 8916 21892 5089 23310 20176 13124 38082 9408 31581 4832 25324 30616 6304 10764 8946 8451 7127 20142 2519 22275 20730 5913 4827 15771 29328 3310 23953 14551 20865 20090 25463 38994 10701 4367 38374 24141 15773 16810 38711 22401 36446 11027 2855 5013 21771 19273 1497 14677 657 30203 29919 14437 14430 31205 1433 6858 25556 9916 8211 22739 2756 7853 17364 23 14563 33645 19999 31310 1329 14193 7144 32253 19960 29140 8782 13399 6192 37358 897 24392 35937 3732 290 2226 10259 10336 8078 34138 37809 28171 30163 20936 24023 13956 36586 38007 18174 1023 21574 38944 37364 34204 12218 10677 20800 35656 26090 29178 28211 21890 36761 1273 23016 1850 11053 10774 13862 11703 32982 36100 7403 4059 26756 11739 30728 27691 34803 7368 13542 28355 36692 16118 18295 32178 30955 37439 10625 91 24175 10201 33071 25176 376 18120 1694 24305 38589 \"}}]}}"
}

class ElasticSuite extends FunSuite {
  test("extract sender data from elastic search results") {
    val d = IO.fromJson(TestElastic.json).head
    assert(ElasticSearch.extract(d, "sender").size == 2)
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

