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
      List("id")
    }
    def fetchOutputs: Iterable[(String, String)] = {
      List(("ParticipantID", "Long"))
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
    val criteria: Map[String, Any] = Map("fromParticipantId" -> id)
    new Friends(IO.cacheAwareRead(bindings, criteria, reader, cache))
  }
  def apply(state: String): Friend = {
    val s = IO.fromFormPost(state)
    new Friend(s("id").asInstanceOf[String].toLong, s("fromParticipantID").asInstanceOf[String].toLong, s("toParticipantID").asInstanceOf[String].toLong) with MySqlWriter with RedisCacheAware
  }
}

case class FriendState(id: Long, fromParticipantID: Long, toParticipantID: Long)

class Friend(id: Long, fromParticipantID: Long, toParticipantID: Long) extends FriendState(id, fromParticipantID, toParticipantID) {
  this: PersistentDataStoreWriter with CacheAware =>

  def save: Unit = {
    val state: Map[String, Any] = Map(
      "fromParticipantID" -> fromParticipantID,
      "toParticipantID" -> toParticipantID
    )
    val criteria: Map[String, Any] = Map(
      "id" -> id
    )
    write(Participant.bindings, state, criteria)
    invalidate(Friends.bindings, criteria)
  }

  def toJson: String = {
    val state: Map[String, Any] = Map(
      "fromParticipantID" -> fromParticipantID,
      "toParticipantID" -> toParticipantID,
      "id" -> id
    )
    IO.toJson(state)
  }

}

class Friends(state: Iterable[Map[String, Any]]) extends Iterator[Friend] {
  def hasNext = state.tail.eq(Nil)
  def next() = {
    val kv = state.head
    new Friend(kv("id").asInstanceOf[Long], kv("fromParticipantID").asInstanceOf[Long], kv("toParticipantID").asInstanceOf[Long]) with MySqlWriter with RedisCacheAware
  }
}