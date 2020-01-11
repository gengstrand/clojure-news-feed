package info.glennengstrand.news.core

trait EntityService[E <: AnyRef] {
  def get(id: Long)(implicit cache: Cache, dao: EntityDAO[E]): Option[E]
  def gets(id: Long)(implicit cache: Cache, dao: EntityDAO[E]): List[E]
  def add(id: Long, e: E)(implicit cache: Cache, dao: EntityDAO[E]): E
}
