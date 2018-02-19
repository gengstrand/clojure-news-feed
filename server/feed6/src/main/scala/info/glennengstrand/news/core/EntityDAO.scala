package info.glennengstrand.news.core

import doobie.imports._
import cats.effect._

trait EntityDAO[E <: AnyRef] {
  def get(id: Long)(implicit db: Transactor[IO]): Option[E]
  def gets(id: Long)(implicit db: Transactor[IO]): List[E]
  def add(entity: E)(implicit db: Transactor[IO]): E
}
