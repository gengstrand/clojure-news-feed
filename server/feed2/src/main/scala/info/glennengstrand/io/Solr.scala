package info.glennengstrand.io

import com.dynamicalsoftware.support.Search
import scala.collection.JavaConverters._

/** helper function for connecting to solr */
object Solr  {
  def getUri(): String = {
    s"http://${IO.settings.get(IO.searchHost).asInstanceOf[String]}:${IO.settings.get(IO.searchPort).asInstanceOf[String]}/${IO.settings.get(IO.searchPath).asInstanceOf[String]}"
  }
  lazy val server = Search.server(getUri())
}

/** thin wrapper around search part of support component */
trait SolrSearcher extends PersistentDataStoreSearcher {
  def search(terms: String): Iterable[Long] = {
    Search.results(Solr.server, terms, 100).listIterator().asScala.toIterable.map { x => x.toLong }
  }
  def index(id: Long, content: String): Unit = {
    Search.add(Solr.server, id, content)
  }

}