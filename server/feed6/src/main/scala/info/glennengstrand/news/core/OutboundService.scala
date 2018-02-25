package info.glennengstrand.news.core

import info.glennengstrand.news.model.{ Outbound, Inbound }
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{ read, write }
import info.glennengstrand.news.db.ElasticSearchDAO
import info.glennengstrand.news.DI._

class OutboundService(friendService: FriendService, inboundService: InboundService) extends ItemService[Outbound] {
  def gets(id: Long)(implicit dao: ItemDAO[Outbound]): List[Outbound] = {
    dao.gets(id)
  }
  def add(o: Outbound)(implicit dao: ItemDAO[Outbound], searchDAO: DocumentDAO[Outbound]): Outbound = {
    o.from match {
      case Some(id) => {
        friendService.gets(id).foreach(f => {
          inboundService.add(Inbound(o.from, f.to, o.occurred, o.subject, o.story))
        })
        searchDAO.index(o)
        dao.add(o)
      }
      case None => o
    }
  }
  def search(keywords: Option[String])(implicit searchDAO: DocumentDAO[Outbound]): List[Int] = {
    keywords match {
      case Some(terms) => searchDAO.search(terms)
      case None => List()
    }

  }
}
