package info.glennengstrand.io

import java.util.{Properties, Calendar}
import org.apache.kafka.clients.producer.{ProducerRecord, Producer, KafkaProducer}
import java.util.logging.{Level, Logger}

object Kafka {
  val log = Logger.getLogger("info.glennengstrand.io.Kafka")
  def connect(): Producer[String, String] = {
    val config = new Properties
    config.setProperty("metadata.broker.list", IO.settings.getProperty(IO.messagingBrokers))
    new KafkaProducer[String, String](config)
  }
  lazy val logger = connect
  def log(topic: String, entity: String, operation: String, duration: Long): Unit = {
    val now = Calendar.getInstance()
    val ts = now.get(Calendar.YEAR).toString + "|" + now.get(Calendar.MONTH).toString + "|" + now.get(Calendar.DAY_OF_MONTH).toString + "|" + now.get(Calendar.HOUR_OF_DAY).toString + "|" + now.get(Calendar.MINUTE).toString
    val msg = ts + "|" + entity + "|" + operation + "|" + duration.toString
    try {
      logger.send(new ProducerRecord[String, String](topic, msg))
    } catch {
      case e: Exception => log.log(Level.SEVERE, "messaging now available\n", e)
    }
  }
}
