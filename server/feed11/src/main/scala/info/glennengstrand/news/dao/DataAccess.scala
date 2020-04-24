package info.glennengstrand.news.dao

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DataAccess[T] {
  def fetchSingle(id: Int): Future[T]
  def insert(t: T): Future[T]
  def fetchMulti(id: Int): Future[Seq[T]] = {
    Future {
      Seq()
    }
  }
  def search(k: String): Future[Seq[String]] = {
    Future {
      Seq()
    }
  }
}