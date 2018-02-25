package info.glennengstrand.news.db

import info.glennengstrand.news.core.{ DocumentDAO, DocumentIdentity }
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.rest.RestStatus
import collection.JavaConversions._

trait ElasticSearchDAO[D <: AnyRef] extends DocumentDAO[D] {
  def client: RestHighLevelClient
  def index(doc: D): Unit = {
    identity match {
      case DocumentIdentity(docIndex, docType, docId, docTerm, docResult) => {
        val request = new IndexRequest(docIndex, docType, docId).source(mapAsJavaMap(source(doc, docId)).asInstanceOf[java.util.Map[java.lang.String, java.lang.Object]])
        client.index(request)
      }
      case _ => Unit
    }
  }
  def search(keywords: String): List[Int] = {
    identity match {
      case DocumentIdentity(docIndex, docType, docId, docTerm, docResult) => {
        val request = new SearchRequest(docIndex).types(docType)
        val builder = new SearchSourceBuilder()
        builder.query(QueryBuilders.termQuery(docTerm, keywords))
        request.source(builder)
        val response = client.search(request)
        response.status() match {
          case RestStatus.OK => {
            val hits = response.getHits
            if (hits.getTotalHits > 0l) {
              hits.getHits.map(sh => sh.getSourceAsMap().get(docResult).asInstanceOf[Int]).toList
            } else {
              List()
            }
          }
          case _ => List()
        }
      }
      case _ => List()
    }

  }
}