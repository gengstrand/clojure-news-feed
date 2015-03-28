package info.glennengstrand.io

import java.util.{Properties, Calendar}
import org.apache.kafka.clients.producer.{ProducerRecord, Producer, KafkaProducer}
import java.util.logging.{Level, Logger}

class Kafka extends PerformanceLogger {
  val log = Logger.getLogger("info.glennengstrand.io.Kafka")
  def connect(): Producer[String, String] = {
    val config = new Properties
    config.setProperty("bootstrap.servers", IO.settings.getProperty(IO.messagingBrokers))
    config.setProperty("metadata.broker.list", IO.settings.getProperty(IO.messagingBrokers))
    config.setProperty("zk.connect", IO.settings.getProperty(IO.zookeeperServers))
    config.setProperty("request.required.acks", "0")
    config.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    new KafkaProducer[String, String](config)
  }
  lazy val logger = connect
  def log(topic: String, entity: String, operation: String, duration: Long): Unit = {
    val msg = logRecord(entity, operation, duration)
    try {
      logger.send(new ProducerRecord[String, String](topic, msg))
    } catch {
      case e: Exception => log.log(Level.SEVERE, "messaging not available\n", e)
    }
  }
}

