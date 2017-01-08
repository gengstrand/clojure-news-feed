package info.glennengstrand.io

import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.impl.DefaultBHttpClientConnection
import org.apache.http.entity.{StringEntity,ContentType}
import org.apache.http.protocol.{HttpRequestExecutor,HttpCoreContext,HttpProcessor,HttpProcessorBuilder,RequestContent,RequestTargetHost,RequestConnControl,RequestUserAgent,RequestExpectContinue}
import org.apache.http.{HttpResponse,HttpHost}
import org.apache.http.util.EntityUtils
import java.util.UUID
import java.net.Socket
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.util.{Try, Success, Failure}

/** helpers functions for interfacing with elastic search */
object ElasticSearch {
  val log = LoggerFactory.getLogger("info.glennengstrand.io.ElasticSearch")
  val JSON_MEDIA_TYPE = "application/json"
  
  /** set up the basic request for loading a document into the index */
  def createIndexRequest(id: Long, key: String): BasicHttpEntityEnclosingRequest = {
    val retVal = new BasicHttpEntityEnclosingRequest("PUT", s"/${IO.settings.get(IO.searchPath).asInstanceOf[String]}/${id}-${key}")
    retVal.addHeader("Accept", JSON_MEDIA_TYPE)
    retVal
  }
  
  /** set up the basic request for searching the index for documents */
  def createSearchRequest(keywords: String): BasicHttpEntityEnclosingRequest = {
    val retVal = new BasicHttpEntityEnclosingRequest("GET", s"/${IO.settings.get(IO.searchPath).asInstanceOf[String]}/_search?q=${keywords}")
    retVal.addHeader("Accept", JSON_MEDIA_TYPE)
    retVal
  }
  
  /** create the document to be indexed */
  def createEntity(id: Long, key: String, story: String): StringEntity = {
    val doc = Map(
        "id" -> key, 
        "sender" -> id,
        "story" -> story
        )
    val content = IO.toJson(doc)
    new StringEntity(content, ContentType.create(JSON_MEDIA_TYPE, "UTF-8"))
  }
  
  /** fetch the child attribute from the parsed JSON results */
  def fetchChild[T](data: Map[String, Any], name: String, default: T): T = {
    data.contains(name) match {
      case true => {
        val r = data(name)
        r match {
          case rm: T => rm
          case _ => default
        }
      }
      case _ => default
    }
  }

  /** extract the desired payload from the search results */
  def extract(response: Map[String, Option[Any]], name: String): List[Long] = {
    val outerHits = fetchChild[Map[String, Any]](response, "hits", Map())
    val innerHits = fetchChild[List[Map[String, Any]]](outerHits, "hits", List())
    innerHits.map(hit => {
      val source = fetchChild[Map[String, Any]](hit, "_source", Map())
      fetchChild[Double](source, name, 0.0d).toLong
    }).toList
  }
}

trait ElasticSearchSearcher extends PersistentDataStoreSearcher {
  def search(terms: String): Iterable[Long] = {
    val req = ElasticSearch.createSearchRequest(terms)
    val client = new DefaultBHttpClientConnection(1000)
    val s = new Socket(IO.settings.get(IO.searchHost).asInstanceOf[String], IO.settings.get(IO.searchPort).asInstanceOf[String].toInt)
    client.bind(s)
    client.sendRequestHeader(req)
    client.flush()
    val retVal = new StringBuilder()
    if (client.isResponseAvailable(5000)) {
      val res = client.receiveResponseHeader()
      val status = res.getStatusLine().getStatusCode
      if (status >= 300) {
        ElasticSearch.log.warn(s"return status = ${status}")
      } else {
        client.receiveResponseEntity(res)
        val response = res.getEntity().getContent
        var c = response.read()
        while (c != -1) {
          retVal.append(c.asInstanceOf[Char])
          c = response.read()
        }
      }
    } else {
      ElasticSearch.log.warn("no response")
    }
    s.close()
    val r = retVal.toString()
    ElasticSearch.extract(IO.fromJson(r).head, "sender")
  }
  def index(id: Long, content: String): Unit = {
    val key = UUID.randomUUID().toString()
    val req = ElasticSearch.createIndexRequest(id, key)
    val se = ElasticSearch.createEntity(id, key, content)
    req.setEntity(se)
    val p = HttpProcessorBuilder.create()
            .add(new RequestContent())
            .add(new RequestTargetHost())
            .add(new RequestConnControl())
            .add(new RequestUserAgent("Feed/1.1"))
            .add(new RequestExpectContinue(true)).build()
    val e = new HttpRequestExecutor()
    val c = HttpCoreContext.create()
    val h = new HttpHost(IO.settings.get(IO.searchHost).asInstanceOf[String], IO.settings.get(IO.searchPort).asInstanceOf[String].toInt)
    c.setTargetHost(h)
    val client = new DefaultBHttpClientConnection(1000)
    val s = new Socket(IO.settings.get(IO.searchHost).asInstanceOf[String], IO.settings.get(IO.searchPort).asInstanceOf[String].toInt)
    client.bind(s)
    e.preProcess(req, p, c)
    val r = e.execute(req, client, c)
    e.postProcess(r, p, c)
    if (r.getStatusLine().getStatusCode >= 300) {
      ElasticSearch.log.warn(r.getStatusLine().toString())
      ElasticSearch.log.warn(EntityUtils.toString(r.getEntity))
    }
    s.close()
  }
  
}
