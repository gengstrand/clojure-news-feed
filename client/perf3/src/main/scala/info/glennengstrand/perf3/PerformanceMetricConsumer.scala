package info.glennengstrand.perf3

import org.apache.kafka.clients.consumer.{KafkaConsumer, ConsumerRecord, ConsumerRecords}
import java.util.Properties
import scala.collection.JavaConversions._
import java.util.logging.{Logger, Level}

/** consumes the stream of messages from the feed topic in kafka and processes them as performance measurements */
class PerformanceMetricConsumer(host: String, topic: String) {
  val log = Logger.getLogger("info.glennengstrand.perf3.PerformanceMetricConsumer")
  def connect: KafkaConsumer[String, String] = {
    val props = new Properties
    props.setProperty("bootstrap.servers", s"${host}:9092")
    props.setProperty("group.id", "news-feed-performance")
    props.setProperty("enable.auto.commit", "true")
    props.setProperty("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RangeAssignor")
    props.setProperty("auto.commit.interval.ms", "1000")
    props.setProperty("session.timeout.ms", "30000")
    props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    new KafkaConsumer[String, String](props)
  }
  lazy val consumer = connect
  def consume(process: PerformanceMeasurement => Unit): Unit = {
    val topics = new java.util.ArrayList[String]
    topics.add(topic)
    consumer.subscribe(topics)
    while (true) {
      val cr = consumer.poll(5000L)
      if (cr != null) {
        if (!cr.isEmpty()) {
          cr.iterator.foreach {
            case record: ConsumerRecord[String, String] => {
              if (record != null) {
                process(PerformanceMeasurement(record.value))
              }
            }
          }
        }
      }
    }
  }
}