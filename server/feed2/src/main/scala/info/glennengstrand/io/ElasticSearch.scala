package info.glennengstrand.io

import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.impl.DefaultBHttpClientConnection
import org.apache.http.entity.{StringEntity,ContentType}
import java.util.UUID

object ElasticSearch {
  val JSON_MEDIA_TYPE = "application/json"
  def createIndexRequest(id: Long, key: String): BasicHttpEntityEnclosingRequest = {
    val retVal = new BasicHttpEntityEnclosingRequest("PUT", s"${IO.settings.get(IO.searchHost).asInstanceOf[String]}/${id}-${key}")
    retVal.addHeader("Accept", JSON_MEDIA_TYPE)
    retVal
  }
  def createSearchRequest(keywords: String): BasicHttpEntityEnclosingRequest = {
    val retVal = new BasicHttpEntityEnclosingRequest("GET", s"${IO.settings.get(IO.searchHost).asInstanceOf[String]}/_search?q=${keywords}")
    retVal.addHeader("Accept", JSON_MEDIA_TYPE)
    retVal
  }
  def createEntity(id: Long, key: String, story: String): StringEntity = {
    val doc = Map(
        "id" -> key, 
        "sender" -> id,
        "story" -> story
        )
    val content = IO.toJson(doc)
    new StringEntity(content, ContentType.create(JSON_MEDIA_TYPE, "UTF-8"))
  }
  def send(request: BasicHttpEntityEnclosingRequest): Unit = {
    val client = new DefaultBHttpClientConnection(1000)
    client.sendRequestEntity(request)
  }
}
class Results(ids: Array[Long]) extends Iterable[java.lang.Long] {
  var idIndex = 0
  def iterator: Iterator[java.lang.Long] = {
    new Iterator[java.lang.Long] {
      def hasNext: Boolean = {
        idIndex < ids.length
      }
      def next: java.lang.Long = {
        val retVal = ids(idIndex)
        idIndex = idIndex + 1
        retVal
      }
    }
  }
}
trait ElasticSearchSearcher extends PersistentDataStoreSearcher {
  def search(terms: String): Iterable[java.lang.Long] = {
    val req = ElasticSearch.createSearchRequest(terms)
    ElasticSearch.send(req)
    val response = req.getEntity().getContent
    var c = response.read()
    val retVal = new StringBuilder()
    while (c != -1) {
      retVal.append(c.asInstanceOf[Char])
      c = response.read()
    }
    new Results(retVal.toString().split(",").map { x => x.toLong })
  }
  def index(id: Long, content: String): Unit = {
    val key = UUID.randomUUID().toString()
    val req = ElasticSearch.createIndexRequest(id, key)
    val se = ElasticSearch.createEntity(id, key, content)
    req.setEntity(se)
    ElasticSearch.send(req)
  }
}