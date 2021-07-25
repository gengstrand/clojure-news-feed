package info.glennengstrand.news.dao

import java.time.{Instant, Duration}
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}

trait Link {
  private val path = raw"/participant/([0-9]+)".r
  private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  def format(d: Instant): String = formatter.print(Duration.ofSeconds(d.getEpochSecond()).toMillis())
  def toLink(id: Long): String = {
    "/participant/%d".format(id)
  }
  def extractId(link: String): Long = {
    link match {
      case path(id) => id.toLong
      case _ => link.toLong
    }
  }
}