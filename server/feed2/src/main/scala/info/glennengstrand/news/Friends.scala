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
      case "mysql" => new Friend(id, Link.toLink(from.longValue), Link.toLink(to.longValue)) with MySqlWriter with JedisCacheAware
      case _ => new Friend(id, Link.toLink(from.longValue), Link.toLink(to.longValue)) with PostgreSqlWriter with JedisCacheAware
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
    create(id, Link.extractId(s("from").asInstanceOf[String]).toInt, Link.extractId(s("to").asInstanceOf[String]).toInt)
  }
}

case class FriendState(id: Int, fromParticipantID: String, toParticipantID: String)

/** represents the friend relationship between two participants */
class Friend(id: Int, fromParticipantID: String, toParticipantID: String) extends FriendState(id, fromParticipantID, toParticipantID) with MicroServiceSerializable {
  this: TransientRelationalDataStoreWriter with CacheAware =>

  /** save to the database and return a new friend object with the newly created primary key */
  def save: Friend = {
    val state: Map[String, Any] = Map(
      "fromParticipantID" -> Link.extractId(fromParticipantID).intValue,
      "toParticipantID" -> Link.extractId(toParticipantID).intValue
    )
    val criteria: Map[String, Any] = Map(
      "FriendsID" -> id
    )
    val result = write(Friends.bindings, state, criteria)
    invalidate(Friends.bindings, criteria)
    val newId = result.getOrElse("id", 0).asInstanceOf[Int]
    Friends.create(newId, Link.extractId(fromParticipantID).intValue, Link.extractId(toParticipantID).intValue)
  }
  
  override def toJson(factory: FactoryClass): String = toJson

  /*
  override def toJson(factory: FactoryClass): String = {
    val fromParticipant = factory.getObject("participant", Link.extractId(fromParticipantID).toInt)
    val toParticipant = factory.getObject("participant", Link.extractId(toParticipantID).toInt)
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
  */

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
  val s: scala.collection.mutable.Set[Long] = scala.collection.mutable.Set()
  var prefetched: Option[Friend] = Option.empty
  def toFriend(kv : Map[String, Any]): Friend = {
    Friends.create(IO.convertToInt(kv("FriendsID")), id.toInt, IO.convertToInt(kv("ParticipantID")))
  }
  def hasNext = {
    i.hasNext match {
      case true => {
        var keepGoing: Boolean = true
        var retVal: Boolean = false
        while (keepGoing) {
          val kv = i.next()
          var f = toFriend(kv)
          val to = Link.extractId(f.toParticipantID)
          if (!s.contains(to)) {
            s.add(to)
            prefetched = Option(f)
            retVal = true
            keepGoing = false
          } else {
            keepGoing = i.hasNext
          }
        }
        retVal
      }
      case _ => false
    }
  }
  def next() = {
    prefetched match {
      case Some(f) => f
      case _ => throw new IndexOutOfBoundsException()
    }
  }
  override def toJson(factory: FactoryClass): String = {
    state.isEmpty match {
      case true => "[]"
      case _ => {
        state.size match {
          case 1 => "[" + toFriend(state.head).toJson(factory) + "]"
          case _ => "[" +  map(f => f.toJson(factory)).reduce(_ + "," + _) + "]"
        }
      }
    }
  }
  override def toJson: String = {
    isEmpty match {
      case true => "[]"
      case _ => "[" +  map(f => f.toJson).reduce(_ + "," + _) + "]"
    }
  }
}