package info.glennengstrand.news.dao

import java.time.{Instant, Duration}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}

object DataAccess {
  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val path = raw"/participant/([0-9]+)".r
}
trait DataAccess[T] {
  def format(d: Instant): String = DataAccess.formatter.print(Duration.ofSeconds(d.getEpochSecond()).toMillis())
  def toLink(id: Long): String = {
    "/participant/%d".format(id)
  }
  def extractId(link: String): Long = {
    link match {
      case DataAccess.path(id) => id.toLong
      case _ => link.toLong
    }
  }
  def fetchSingle(id: Int): Future[T]
  def insert(t: T): Future[T]
  def fetchMulti(id: Int): Future[Seq[T]] = {
    Future.successful(Seq())
  }
}