package info.glennengstrand.news.core

trait EntityService[E <: AnyRef] {
  def get(id: Long)(implicit cache: Cache, dao: EntityDAO[E]): Option[E]
  def gets(id: Long)(implicit cache: Cache, dao: EntityDAO[E]): List[E]
  def add(e: E)(implicit dao: EntityDAO[E]): E
}
