package info.glennengstrand.news.dao

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DataAccess[T] {
  private val path = raw"/participant/([0-9]+)".r
  def toLink(id: Long): String = {
    "/participant/%d".format(id)
  }
  def extractId(link: String): Long = {
    link match {
      case path(id) => id.toLong
      case _ => link.toLong
    }
  }
  def fetchSingle(id: Int): Future[T]
  def insert(t: T): Future[T]
  def fetchMulti(id: Int): Future[Seq[T]] = {
    Future {
      Seq()
    }
  }
}