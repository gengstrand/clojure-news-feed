package info.glennengstrand.io

import com.dynamicalsoftware.support.Search

object Solr  {
  lazy val server = Search.server(IO.settings.get(IO.searchHost).asInstanceOf[String])
}

trait SolrSearcher extends PersistentDataStoreSearcher {
  def search(terms: String): Iterable[Long] = {
    Search.results(Solr.server, terms, 100).listIterator().asInstanceOf[Iterable[Long]]
  }
  def index(id: Long, content: String): Unit = {
    Search.add(Solr.server, id, content)
  }

}