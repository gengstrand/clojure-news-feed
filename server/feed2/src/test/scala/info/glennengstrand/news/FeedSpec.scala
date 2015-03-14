package info.glennengstrand.news

import info.glennengstrand.io._
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest

trait MockWriter extends PersistentDataStoreWriter {
  def write(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Map[String, Any] = {
    Map()
  }
}

trait MockCacheAware extends CacheAware {
  def load(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]] = {
    // TODO: implement this
    List(Map())
  }
  def store(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
    // TODO: implement this

  }
  def store(o: PersistentDataStoreBindings, state: Iterable[Map[String, Any]], criteria: Map[String, Any]): Unit = {
    // TODO: implement this

  }
  def append(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
    // TODO: implement this

  }
  def invalidate(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Unit = {
    // TODO: implement this

  }
}

class MockFactoryClass extends FactoryClass {

  def getParticipant(id: Long): Participant = {
    new Participant(id, "test") with MockWriter with MockCacheAware
  }
  def getParticipant(state: String): Participant = {
    val s = IO.fromFormPost(state)
    new Participant(s("id").asInstanceOf[String].toLong, s("name").asInstanceOf[String]) with MockWriter with MockCacheAware
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
    new Friend(s("FriendsID").asInstanceOf[String].toLong, s("FromParticipantID").asInstanceOf[String].toLong, s("ToParticipantID").asInstanceOf[String].toLong) with MockWriter with MockCacheAware
  }
  def getObject(name: String, id: Long): Option[Object] = {
    name match {
      case "participant" => Some(getParticipant(id))
      case "friends" => Some(getFriends(id))
      case _ => None
    }
  }
  def getObject(name: String, state: String): Option[Object] = {
    name match {
      case "participant" => Some(getParticipant(state))
      case "friend" => Some(getFriend(state))
      case _ => None
    }
  }
}

class FeedSpec extends Specification with Specs2RouteTest with Feed {
  def actorRefFactory = system
  Feed.factory = new MockFactoryClass

  "Feed" should {

    "return the correct data when fetching a participant" in {
      Get("/participant/2") ~> myRoute ~> check {
        responseAs[String] must contain("test")
      }
    }

    "return the correct data when fetching friends" in {
      Get("/friends/1") ~> myRoute ~> check {
        responseAs[String] must contain("2")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "process post requests to create a new participant properly" in {
      Post("/participant.new", "id=2&name=smith") ~> myRoute ~> check {
        responseAs[String] must contain("smith")
      }
    }
  }
}
