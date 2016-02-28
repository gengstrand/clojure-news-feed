package info.glennengstrand.perf3

import akka.actor.Actor
import scalaj.http.{HttpRequest, Http}
import scala.util.{Try, Success, Failure}
import java.util.logging.{Logger, Level}

/** static helper routines for sending upsert requests to elastic search */
object ElasticSearchRequest {
  val log = Logger.getLogger("info.glennengstrand.perf3.ElasticSearchRequest")
  def generate(host: String, ts: String, entity: String, operation: String, throughput: Int, mean: Long, median: Long, upper5: Long): (String, String) = {
    val body = new StringBuilder
    body.append("{\"ts\":\"")
    body.append(ts)
    body.append("\",\"entity\":\"")
    body.append(entity)
    body.append("\",\"operation\":\"")
    body.append(operation)
    body.append("\",\"throughput\":")
    body.append(throughput.toString())
    body.append(",\"mean\":")
    body.append(mean.toString())
    body.append(",\"median\":")
    body.append(median.toString())
    body.append(",\"upper5\":")
    body.append(upper5.toString())
    body.append("}")
    val url = s"http://${host}:9200/performance/feed/${entity}-${operation}-${ts}"
    ( url, body.toString() )    
  }
}

/** responsible for interacting with elastic search */
class ElasticSearchRequest(host: String) {
  def update(ts: String, entity: String, operation: String, throughput: Int, mean: Long, median: Long, upper5: Long): Unit = {
    val ( url, body ) = ElasticSearchRequest.generate(host, ts, entity, operation, throughput, mean, median, upper5)
    ElasticSearchRequest.log.finest(s"url: ${url}")
    ElasticSearchRequest.log.finest(s"body: ${body}")
    val r = Try {
      Http(url).postData(body).method("PUT").asString
    }
    r match {
      case Success(response) => {
        response.is2xx match {
          case true => ElasticSearchRequest.log.finest("request successfully sent")
          case _ => {
            ElasticSearchRequest.log.isLoggable(Level.FINEST) match {
              case false => {
                ElasticSearchRequest.log.warning(s"url: ${url}")
                ElasticSearchRequest.log.warning(s"body: ${body}")
                ElasticSearchRequest.log.warning(response.statusLine)
              }
              case _ => ElasticSearchRequest.log.warning(response.statusLine)
            }
          }
        }
      }
      case Failure(msg) => {
        ElasticSearchRequest.log.warning(s"url: ${url}")
        ElasticSearchRequest.log.warning(s"body: ${body}")
        ElasticSearchRequest.log.warning(msg.getLocalizedMessage)
      }
    }
  }
}

/** represents an update of performance metric information */
case class PerformanceMetricUpdate(ts: String, entity: String, operation: String, throughput: Int, mean: Long, median: Long, upper5: Long)

/** sends performance metric updates to monitoring repository */
class PerformanceMetricSummary(host: String) extends Actor {
  val server = new ElasticSearchRequest(host)
  def receive = {
    case PerformanceMetricUpdate(ts, entity, operation, throughput, mean, median, upper5) => {
      server.update(ts, entity, operation, throughput, mean, median, upper5)
    }
    case _ => ElasticSearchRequest.log.warning("invalid message")
  }
}