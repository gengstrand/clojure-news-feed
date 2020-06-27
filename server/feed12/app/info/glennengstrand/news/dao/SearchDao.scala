package info.glennengstrand.news.dao

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import java.util.UUID

import scala.concurrent.Future
import info.glennengstrand.news.model._
import scala.jdk.CollectionConverters._

import org.elasticsearch.client.{ RestHighLevelClient, RestClient, RequestOptions }
import org.apache.http.HttpHost
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.ActionListener
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.rest.RestStatus

class SearchExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait SearchDao {
  def index(doc: Map[String, Object])(implicit mc: MarkerContext): Unit

  def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]]
}

@Singleton
class SearchDaoImpl @Inject()()(implicit ec: SearchExecutionContext)
    extends SearchDao {

  private val logger = Logger(this.getClass)
  private val searchHost = sys.env.get("SEARCH_HOST").getOrElse("localhost")
  private val docType = "stories"
  private val docIndex = "feed"
  private val listener = new MyActionListener
  private def connect: RestHighLevelClient = {
    new RestHighLevelClient(RestClient.builder(new HttpHost(searchHost, 9200)))
  }
  private lazy val es = connect

  private class MyActionListener extends ActionListener[IndexResponse] {
     def onFailure(e: Exception): Unit = {
       logger.warn("cannot index create request: ", e)
     }
     def onResponse(resp: IndexResponse): Unit = {
       resp.status() match {
         case RestStatus.OK => {
           logger.debug("OK from elasticsearch index request")
         }
         case RestStatus.CREATED => {
           logger.debug("elasticsearch document created")
         }
         case _ => {
           logger.warn(resp.toString())
         }
       }
     }
  }
  
  override def index(doc: Map[String, Object]) (implicit mc: MarkerContext): Unit = {
    val docId = UUID.randomUUID().toString()
    val request = new IndexRequest(docIndex, docType, docId).source((doc + ("id" -> docId)).asJava.asInstanceOf[java.util.Map[java.lang.String, java.lang.Object]])
    es.indexAsync(request, RequestOptions.DEFAULT, listener)
  }

  override def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]] = {
    Future {
      val request = new SearchRequest(docIndex).types(docType)
      val builder = new SearchSourceBuilder()
      builder.query(QueryBuilders.termQuery("story", keywords))
      request.source(builder)
      val response = es.search(request, RequestOptions.DEFAULT)
      response.status() match {
        case RestStatus.OK => {
          val hits = response.getHits
          if (hits.getTotalHits.equals(0L)) {
            Seq()
          } else {
            hits.getHits.map(sh => sh.getSourceAsMap().get("sender").asInstanceOf[String]).toSeq
          }
        }
        case _ => Seq()
      }
    }
  }

}

object MockSearchDaoImpl {
  val test = Seq("/participant/1")
}

@Singleton
class MockSearchDaoImpl @Inject()()(implicit ec: SearchExecutionContext)
    extends SearchDao {

  private val logger = Logger(this.getClass)

  override def index(doc: Map[String, Object]) (implicit mc: MarkerContext): Unit = {
      logger.trace(s"index: doc = $doc")
  }

  override def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]] = {
    Future {
      logger.trace(s"search: keywords = $keywords")
      MockSearchDaoImpl.test
    }
  }

}
