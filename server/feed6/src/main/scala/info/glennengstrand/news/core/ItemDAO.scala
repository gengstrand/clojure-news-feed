package info.glennengstrand.news.core

import java.util.Date
import com.datastax.driver.core.Session
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}

object ItemDAO {
  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
}

trait ItemDAO[I] {
  def gets(id: Long)(implicit db: Session): List[I]
  def add(item: I)(implicit db: Session): I
  def format(d: Date): String = ItemDAO.formatter.print(d.getTime())
}