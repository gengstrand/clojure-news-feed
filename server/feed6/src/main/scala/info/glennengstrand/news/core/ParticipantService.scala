package info.glennengstrand.news.core

import info.glennengstrand.news.model.Participant
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{ read, write }
import info.glennengstrand.news.DI._

object ParticipantService {
  val namespace = "Participant::"
}

class ParticipantService extends EntityService[Participant] {
  def get(id: Long)(implicit cache: Cache, dao: EntityDAO[Participant]): Option[Participant] = {
    val k = ParticipantService.namespace + id.toString()
    val p = cache.get(k)
    p match {
      case Some(v) => Option(read[Participant](v))
      case None => {
        val retVal = dao.get(id)
        retVal match {
          case Some(r) => {
            cache.set(k, write[Participant](r))
            retVal
          }
          case None => None
        }
      }
    }
  }
  def gets(id: Long)(implicit cache: Cache, dao: EntityDAO[Participant]): List[Participant] = {
    List()
  }
  def add(id: Long, p: Participant)(implicit cache: Cache, dao: EntityDAO[Participant]): Participant = {
    val k = ParticipantService.namespace + p.id.toString()
    cache.delete(k)
    dao.add(p)
  }
}
