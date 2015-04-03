package info.glennengstrand.io

import com.dynamicalsoftware.support.Search
import scala.collection.JavaConverters._

object Solr  {
  lazy val server = Search.server(IO.settings.get(IO.searchHost).asInstanceOf[String])
}

trait SolrSearcher extends PersistentDataStoreSearcher {
  def search(terms: String): Iterable[java.lang.Long] = {
    Search.results(Solr.server, terms, 100).listIterator().asScala.toIterable
  }
  def index(id: Long, content: String): Unit = {
    Search.add(Solr.server, id, content)
  }

}