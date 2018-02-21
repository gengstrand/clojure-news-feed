package info.glennengstrand.news.core

import info.glennengstrand.news.model.Inbound
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{ read, write }
import info.glennengstrand.news.DI._

class InboundService extends ItemService[Inbound] {
  def gets(id: Long)(implicit dao: ItemDAO[Inbound]): List[Inbound] = {
    dao.gets(id)
  }
  def add(item: Inbound)(implicit dao: ItemDAO[Inbound]): Inbound = {
    dao.add(item)
  }

}
