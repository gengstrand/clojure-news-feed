package info.glennengstrand.news

import info.glennengstrand.io._

object Participant {
  val reader: PersistentDataStoreReader = new MySqlReader
  val cache: CacheAware = new RedisCache
  class ParticipantBindings extends PersistentDataStoreBindings {
    def entity: String = {
      "Participant"
    }
    def fetchInputs: Iterable[String] = {
      List("id")
    }
    def fetchOutputs: Iterable[(String, String)] = {
      List(("Moniker", "String"))
    }
    def upsertInputs: Iterable[String] = {
      List("name")
    }
    def upsertOutputs: Iterable[(String, String)] = {
      List(("id", "Long"))
    }
    def fetchOrder: Map[String, String] = {
      Map()
    }
  }
  val bindings = new ParticipantBindings
  def apply(id: Long) : Participant = {
    val criteria: Map[String, Any] = Map("id" -> id)
    val state: Map[String, Any] = IO.cacheAwareRead(bindings, criteria, reader, cache)
    new Participant(id, state("Moniker").asInstanceOf[String]) with MySqlWriter with RedisCacheAware
  }
  def apply(state: String): Participant = {
    val s = IO.fromFormPost(state)
    new Participant(s("id").asInstanceOf[String].toLong, s("name").asInstanceOf[String]) with MySqlWriter with RedisCacheAware
  }
}

case class ParticipantState(id: Long, name: String)

class Participant(id: Long, name: String) extends ParticipantState(id, name) {
  this: PersistentDataStoreWriter with CacheAware =>

  def save: Unit = {
    val state: Map[String, Any] = Map(
      "name" -> name
    )
    val criteria: Map[String, Any] = Map(
      "id" -> id
    )
    write(Participant.bindings, state, criteria)
    invalidate(Participant.bindings, criteria)
  }

  def toJson: String = {
    val state: Map[String, Any] = Map(
      "name" -> name,
      "id" -> id
    )
    IO.toJson(state)
  }

}
