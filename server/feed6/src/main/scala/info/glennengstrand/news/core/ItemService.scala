package info.glennengstrand.news.core

trait ItemService[I] {
  def gets(id: Long)(implicit dao: ItemDAO[I]): List[I]
  def add(item: I)(implicit dao: ItemDAO[I]): I
  def search(keywords: Option[String]): List[Int]
}