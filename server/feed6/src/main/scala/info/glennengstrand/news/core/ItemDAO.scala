package info.glennengstrand.news.core

import com.datastax.driver.core.Session

trait ItemDAO[I] {
  def gets(id: Long)(implicit db: Session): List[I]
  def add(item: I)(implicit db: Session): I
}