package info.glennengstrand.news.dao

import io.vertx.lang.scala.ScalaLogger
import org.elasticsearch.client.{ RestHighLevelClient, RestClient, RequestOptions }
import org.apache.http.HttpHost
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.ActionListener
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.rest.RestStatus
import collection.JavaConversions._
import java.util.UUID

class ElasticSearchDao {
  private val LOGGER = ScalaLogger.getLogger("info.glennengstrand.news.dao.ElasticSearchDao")
  private val searchHost = sys.env.get("SEARCH_HOST").getOrElse("localhost")
  private val docIndex = "feed"
  private val docType = "stories"
  private val listener = new MyActionListener
  private def connect: RestHighLevelClient = {
    new RestHighLevelClient(RestClient.builder(new HttpHost(searchHost, 9200)))
  }
  private lazy val es = connect
  def index(doc: Map[String, Object]): Unit = {
    val docId = UUID.randomUUID().toString()
    val request = new IndexRequest(docIndex, docType, docId).source(mapAsJavaMap(doc + ("id" -> docId)).asInstanceOf[java.util.Map[java.lang.String, java.lang.Object]])
    es.indexAsync(request, RequestOptions.DEFAULT, listener)
  }
  def search(keywords: String): Seq[String] = {
    LOGGER.warn("real elastic search dao search")
    val request = new SearchRequest(docIndex).types(docType)
    val builder = new SearchSourceBuilder()
    builder.query(QueryBuilders.termQuery("story", keywords))
    request.source(builder)
    val response = es.search(request, RequestOptions.DEFAULT)
    response.status() match {
      case RestStatus.OK => {
        val hits = response.getHits
        if (hits.getTotalHits.equals(0l)) {
          Seq()
        } else {
          hits.getHits.map(sh => sh.getSourceAsMap().get("sender").asInstanceOf[String]).toSeq
        }
      }
      case _ => Seq()
    }
  }
  private class MyActionListener extends ActionListener[IndexResponse] {
     def onFailure(e: Exception): Unit = {
       LOGGER.warn("cannot index create request: ", e)
     }
     def onResponse(resp: IndexResponse): Unit = {
       resp.status() match {
         case RestStatus.OK => {
           LOGGER.debug("OK from elasticsearch index request")
         }
         case RestStatus.CREATED => {
           LOGGER.debug("elasticsearch document created")
         }
         case _ => {
           LOGGER.warn(resp.toString())
         }
       }
     }
  }
}