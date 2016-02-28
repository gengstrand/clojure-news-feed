package info.glennengstrand.perf3

import akka.actor.{ActorSystem, ActorRef, Props}
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import org.joda.time.Instant
import java.util.logging.{Logger, Level}

/** consume performance messages from kafka topic and aggregate results to elastic search once a minute */
object Main extends App {
  val log = Logger.getLogger("info.glennengstrand.perf3.Main")
  val system = ActorSystem("news-feed-performance")
  val metrics: Map[String, ActorRef] = Map (
    "friends|post" -> system.actorOf(Props(new PerformanceMetric("friends", "post"))), 
    "outbound|post" -> system.actorOf(Props(new PerformanceMetric("outbound", "post"))), 
    "outbound|search" -> system.actorOf(Props(new PerformanceMetric("outbound", "search"))), 
    "participant|post" -> system.actorOf(Props(new PerformanceMetric("participant", "post")))
  )
  lazy val elasticSearchActor = system.actorOf(Props(new PerformanceMetricSummary(args(1))))

  def printUsage: Unit = {
    println("java -jar news-feed-performance-assembly-1.0.jar kafka_ip_address elastic_search_ip_address")
  }

  args.size match {
    case 0 => printUsage
    case 1 => printUsage
    case _ => {
      val kafkaHost = args(0)
      val consumer = new PerformanceMetricConsumer(kafkaHost, "feed")
      import system.dispatcher
      val oneMinute = FiniteDuration(60L, TimeUnit.SECONDS)
      system.scheduler.schedule(oneMinute, oneMinute) {
        val event = MetricAggregationEvent(Instant.now().toString())
        metrics.values.foreach(metric => metric ! event)
      }
      consumer.consume { metric => {
          log.finest(metric.toString())
          val metricActor = metrics.get(metric.key)
          metricActor match {
            case Some(actor) => actor ! metric
            case None => log.warning(s"not tracking metric ${metric.key}")
          }
        }
      }
    }
  }
}
