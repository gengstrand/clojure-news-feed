package info.glennengstrand.news

import info.glennengstrand.io._

/** participant object creation helper functions */
object Participant {
  lazy val reader = IO.getReader
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
      List(("id", "Int"))
    }
    def fetchOrder: Map[String, String] = {
      Map()
    }
  }
  val bindings = new ParticipantBindings
  def create(id: Int, name: String): Participant = {
    IO.settings.getProperty(IO.jdbcVendor) match {
      case "mysql" => new Participant(id, name) with MySqlWriter with JedisCacheAware
      case _ => new Participant(id, name) with PostgreSqlWriter with JedisCacheAware
    }
  }
  def apply(id: Int) : Participant = {
    val criteria: Map[String, Any] = Map("id" -> id)
    val state: Map[String, Any] = IO.cacheAwareRead(bindings, criteria, reader, cache).head
    create(id, state("Moniker").asInstanceOf[String])
  }
  def apply(state: String): Participant = {
    val s = IO.fromFormPost(state)
    val id = s.contains("id") match {
      case true => s("id").asInstanceOf[String].toInt
      case _ => 0
    }
    create(id, s("name").asInstanceOf[String])
  }
}

case class ParticipantState(id: Int, name: String)

/** represents a participant who may have friends and news feed */
class Participant(id: Int, name: String) extends ParticipantState(id, name) with MicroServiceSerializable {
  this: PersistentRelationalDataStoreWriter with CacheAware =>

  /** save participant state to db */
  def save: Participant = {
    val state: Map[String, Any] = Map(
      "name" -> name
    )
    val criteria: Map[String, Any] = Map(
      "id" -> id
    )
    val result = write(Participant.bindings, state, criteria)
    val newId = result.getOrElse("id", 0l).asInstanceOf[Int]
    invalidate(Participant.bindings, criteria)
    Participant.create(newId, name)
  }

  override def toJson: String = {
    val state: Map[String, Any] = Map(
      "name" -> name,
      "id" -> id
    )
    IO.toJson(state)
  }

  override def toJson(factory: FactoryClass): String = toJson

}
