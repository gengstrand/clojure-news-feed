package info.glennengstrand.news

import info.glennengstrand.io._

/** helper functions for friend object creation */
object Friends {
  val reader: PersistentDataStoreReader = new MySqlReader
  val cache: CacheAware = new RedisCache
  class FriendsBindings extends PersistentDataStoreBindings {
    def entity: String = {
      "Friends"
    }
    def fetchInputs: Iterable[String] = {
      List("ParticipantID")
    }
    def fetchOutputs: Iterable[(String, String)] = {
      List(("FriendsID", "Long"), ("ParticipantID", "Long"))
    }
    def upsertInputs: Iterable[String] = {
      List("fromParticipantID", "toParticipantID")
    }
    def upsertOutputs: Iterable[(String, String)] = {
      List(("id", "Long"))
    }
    def fetchOrder: Map[String, String] = {
      Map()
    }
  }
  val bindings = new FriendsBindings
  def create(id: Long, from: Long, to: Long): Friend = {
    IO.settings.getProperty(IO.jdbcVendor) match {
      case "mysql" => new Friend(id, from, to) with MySqlWriter with RedisCacheAware
      case _ => new Friend(id, from, to) with PostgreSqlWriter with RedisCacheAware
    }
  }
  def apply(id: Long) : Friends = {
    val criteria: Map[String, Any] = Map("ParticipantID" -> id)
    new Friends(id, IO.cacheAwareRead(bindings, criteria, reader, cache))
  }
  def apply(state: String): Friend = {
    val s = IO.fromFormPost(state)
    val id = s.contains("FriendsID") match {
      case true => s("FriendsID").asInstanceOf[String].toLong
      case _ => 0l
    }
    create(id, s("from").asInstanceOf[String].toLong, s("to").asInstanceOf[String].toLong)
  }
}

case class FriendState(id: Long, fromParticipantID: Long, toParticipantID: Long)

/** represents the friend relationship between two participants */
class Friend(id: Long, fromParticipantID: Long, toParticipantID: Long) extends FriendState(id, fromParticipantID, toParticipantID) with MicroServiceSerializable {
  this: PersistentRelationalDataStoreWriter with CacheAware =>

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
    val newId = result.getOrElse("id", 0l).asInstanceOf[Long]
    Friends.create(newId, fromParticipantID, toParticipantID)
  }

  override def toJson(factory: FactoryClass): String = {
    val state: Map[String, Any] = Map(
      "from" -> factory.getObject("participant", fromParticipantID).get.asInstanceOf[MicroServiceSerializable],
      "to" -> factory.getObject("participant", toParticipantID).get.asInstanceOf[MicroServiceSerializable],
      "id" -> id
    )
    IO.toJson(state)
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
class Friends(id: Long, state: Iterable[Map[String, Any]]) extends Iterator[Friend] with MicroServiceSerializable {
  val i = state.iterator
  def hasNext = i.hasNext
  def next() = {
    val kv = i.next()
    Friends.create(IO.convertToLong(kv("FriendsID")), id, IO.convertToLong(kv("ParticipantID")))
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