package info.glennengstrand.news

import info.glennengstrand.io._


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
    IO.settings.getProperty(IO.jdbcVendor) match {
      case "mysql" => new Friend(id, s("from").asInstanceOf[String].toLong, s("to").asInstanceOf[String].toLong) with MySqlWriter with RedisCacheAware
      case _ => new Friend(id, s("from").asInstanceOf[String].toLong, s("to").asInstanceOf[String].toLong) with PostgreSqlWriter with RedisCacheAware
    }

  }
}

case class FriendState(id: Long, fromParticipantID: Long, toParticipantID: Long)

class Friend(id: Long, fromParticipantID: Long, toParticipantID: Long) extends FriendState(id, fromParticipantID, toParticipantID) {
  this: PersistentDataStoreWriter with CacheAware =>

  def save: Friend = {
    val state: Map[String, Any] = Map(
      "FromParticipantID" -> fromParticipantID,
      "ToParticipantID" -> toParticipantID
    )
    val criteria: Map[String, Any] = Map(
      "FriendsID" -> id
    )
    val result = write(Friends.bindings, state, criteria)
    invalidate(Friends.bindings, criteria)
    val newId = result.get("id").asInstanceOf[Long]
    new Friend(newId, fromParticipantID, toParticipantID) with MySqlWriter with RedisCacheAware
  }

  def toJson(factory: FactoryClass): String = {
    val state: Map[String, Any] = Map(
      "from" -> factory.getObject("participant", fromParticipantID).get.asInstanceOf[Participant].toJson,
      "to" -> factory.getObject("participant", toParticipantID).get.asInstanceOf[Participant].toJson,
      "id" -> id
    )
    IO.toJson(state)
  }

}

class Friends(id: Long, state: Iterable[Map[String, Any]]) extends Iterator[Friend] {
  val i = state.iterator
  def hasNext = i.hasNext
  def next() = {
    val kv = i.next()
    IO.settings.getProperty(IO.jdbcVendor) match {
      case "mysql" => new Friend(IO.convertToLong(kv("FriendsID")), id, IO.convertToLong(kv("ParticipantID"))) with MySqlWriter with RedisCacheAware
      case _ => new Friend(IO.convertToLong(kv("FriendsID")), id, IO.convertToLong(kv("ParticipantID"))) with PostgreSqlWriter with RedisCacheAware
    }
  }
  def toJson(factory: FactoryClass): String = {
    isEmpty match {
      case true => "[]"
      case _ => "[" +  map(f => f.toJson(factory)).reduce(_ + "," + _) + "]"
    }
  }
}