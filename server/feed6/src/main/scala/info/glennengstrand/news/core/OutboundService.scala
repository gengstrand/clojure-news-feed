package info.glennengstrand.news.core

import info.glennengstrand.news.model.Outbound
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{ read, write }
import info.glennengstrand.news.DI._

class OutboundService {
  def get(id: Long): Outbound = {
    Outbound(None, None, None, None)
  }
  def add(p: Outbound): Outbound = {
    Outbound(None, None, None, None)
  }
  def search(keywords: Option[String]): List[Int] = {
    List()
  }
}
