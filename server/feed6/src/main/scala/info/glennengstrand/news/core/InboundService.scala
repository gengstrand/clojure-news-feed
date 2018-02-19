package info.glennengstrand.news.core

import info.glennengstrand.news.model.Inbound
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{ read, write }
import info.glennengstrand.news.DI._

class InboundService {
  def get(id: Long): Inbound = {
    Inbound(None, None, None, None, None)
  }

}
