package info.glennengstrand.news.core

import info.glennengstrand.news.model.Friend
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{ read, write }
import info.glennengstrand.news.DI._

object FriendService {
  val namespace = "Friend::"
}

class FriendService extends EntityService[Friend] {
  def get(id: Long)(implicit cache: Cache, dao: EntityDAO[Friend]): Option[Friend] = {
    None
  }
  def gets(id: Long)(implicit cache: Cache, dao: EntityDAO[Friend]): List[Friend] = {
    val k = FriendService.namespace + id.toString()
    val p = cache.get(k)
    p match {
      case Some(v) => read[List[Friend]](v)
      case None => {
        val retVal = dao.gets(id).distinct
        cache.set(k, write[List[Friend]](retVal))
        retVal
      }
    }
  }
  def add(p: Friend)(implicit cache: Cache, dao: EntityDAO[Friend]): Friend = {
    val fk = FriendService.namespace + p.from.toString()
    cache.delete(fk)
    val tk = FriendService.namespace + p.to.toString()
    cache.delete(tk)
    dao.add(p)
  }
}
