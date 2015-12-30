package info.glennengstrand.news

import info.glennengstrand.io._

/** helper functions for friend object creation */
object Friends {
  lazy val reader = IO.getReader
  val cache: CacheAware = new RedisCache
  class FriendsBindings extends PersistentDataStoreBindings {
    def entity: String = {
      "Friends"
    }
    def fetchInputs: Iterable[String] = {
      List("ParticipantID")
    }
    def fetchOutputs: Iterable[(String, String)] = {
      List(("FriendsID", "Int"), ("ParticipantID", "Int"))
    }
    def upsertInputs: Iterable[String] = {
      List("fromParticipantID", "toParticipantID")
    }
    def upsertOutputs: Iterable[(String, String)] = {
      List(("id", "Int"))
    }
    def fetchOrder: Map[String, String] = {
      Map()
    }
  }
  val bindings = new FriendsBindings
  def create(id: Int, from: Int, to: Int): Friend = {
    IO.settings.getProperty(IO.jdbcVendor) match {
      case "mysql" => new Friend(id, from, to) with MySqlWriter with JedisCacheAware
      case _ => new Friend(id, from, to) with PostgreSqlWriter with JedisCacheAware
    }
  }
  def apply(id: Int) : Friends = {
    val criteria: Map[String, Any] = Map("ParticipantID" -> id.toInt)
    new Friends(id, IO.cacheAwareRead(bindings, criteria, reader, cache))
  }
  def apply(state: String): Friend = {
    val s = IO.fromFormPost(state)
    val id = s.contains("FriendsID") match {
      case true => s("FriendsID").asInstanceOf[String].toInt
      case _ => 0
    }
    create(id, s("from").asInstanceOf[String].toInt, s("to").asInstanceOf[String].toInt)
  }
}

case class FriendState(id: Int, fromParticipantID: Long, toParticipantID: Long)

/** represents the friend relationship between two participants */
class Friend(id: Int, fromParticipantID: Int, toParticipantID: Int) extends FriendState(id, fromParticipantID, toParticipantID) with MicroServiceSerializable {
  this: TransientRelationalDataStoreWriter with CacheAware =>

  /** save to the database and return a new friend object with the newly created primary key */
  def save: Friend = {
    val state: Map[String, Any] = Map(
      "fromParticipantID" -> fromParticipantID,
      "toParticipantID" -> toParticipantID
    )
    val criteria: Map[String, Any] = Map(
      "FriendsID" -> id
    )
    val result = write(Friends.bindings, state, criteria)
    invalidate(Friends.bindings, criteria)
    val newId = result.getOrElse("id", 0).asInstanceOf[Int]
    Friends.create(newId, fromParticipantID, toParticipantID)
  }

  override def toJson(factory: FactoryClass): String = {
    val fromParticipant = factory.getObject("participant", fromParticipantID)
    val toParticipant = factory.getObject("participant", toParticipantID)
    fromParticipant match {
      case None => toJson
      case _ => {
        toParticipant match {
          case None => toJson
          case _ => {
            val state: Map[String, Any] = Map(
              "from" -> fromParticipant.get.asInstanceOf[MicroServiceSerializable],
              "to" -> toParticipant.get.asInstanceOf[MicroServiceSerializable],
              "id" -> id
            )
            IO.toJson(state)
          }
        }
      }
    }
  }

  override def toJson: String = {
    val state: Map[String, Any] = Map(
      "from" -> fromParticipantID,
      "to" -> toParticipantID,
      "id" -> id
    )
    IO.toJson(state)

  }

}

/** collection representing all the friends of a particular participant */
class Friends(id: Int, state: Iterable[Map[String, Any]]) extends Iterator[Friend] with MicroServiceSerializable {
  val i = state.iterator
  def hasNext = i.hasNext
  def next() = {
    val kv = i.next()
    Friends.create(IO.convertToInt(kv("FriendsID")), id.toInt, IO.convertToInt(kv("ParticipantID")))
  }
  override def toJson(factory: FactoryClass): String = {
    isEmpty match {
      case true => "[]"
      case _ => "[" +  map(f => f.toJson(factory)).reduce(_ + "," + _) + "]"
    }
  }
  override def toJson: String = {
    isEmpty match {
      case true => "[]"
      case _ => "[" +  map(f => f.toJson).reduce(_ + "," + _) + "]"
    }
  }
}