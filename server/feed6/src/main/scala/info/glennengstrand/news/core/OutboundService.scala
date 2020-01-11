package info.glennengstrand.news.core

import info.glennengstrand.news.model.{ Outbound, Inbound }
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{ read, write }
import info.glennengstrand.news.db.ElasticSearchDAO
import info.glennengstrand.news.DI._
import info.glennengstrand.news.Link

class OutboundService(friendService: FriendService, inboundService: InboundService) extends ItemService[Outbound] {
  def gets(id: Long)(implicit dao: ItemDAO[Outbound]): List[Outbound] = {
    dao.gets(id)
  }
  def add(id: Long, o: Outbound)(implicit dao: ItemDAO[Outbound], searchDAO: DocumentDAO[Outbound]): Outbound = {
    o.from match {
      case Some(fid) => {
        friendService.gets(id).foreach(f => {
          inboundService.add(id, Inbound(o.from, f.to, o.occurred, o.subject, o.story))
        })
        searchDAO.index(o)
        dao.add(o)
      }
      case None => o
    }
  }
  def search(keywords: Option[String])(implicit searchDAO: DocumentDAO[Outbound]): List[String] = {
    keywords match {
      case Some(terms) => searchDAO.search(terms).map(p => Link.toLink(p))
      case None => List()
    }

  }
}
