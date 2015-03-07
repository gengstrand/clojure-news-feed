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
  def load(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Map[String, Any] = {
    // TODO: implement this
    Map()
  }
  def store(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
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
  def getObject(name: String, id: Long): Option[Object] = {
    name match {
      case "participant" => Some(getParticipant(id))
      case _ => None
    }
  }
  def getObject(name: String, state: String): Option[Object] = {
    name match {
      case "participant" => Some(getParticipant(state))
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
