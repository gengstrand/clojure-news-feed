package info.glennengstrand.news.core

trait ItemService[I <: AnyRef] {
  def gets(id: Long)(implicit dao: ItemDAO[I]): List[I]
  def add(item: I)(implicit dao: ItemDAO[I], searchDAO: DocumentDAO[I]): I
  def search(keywords: Option[String])(implicit searchDAO: DocumentDAO[I]): List[Int]
}