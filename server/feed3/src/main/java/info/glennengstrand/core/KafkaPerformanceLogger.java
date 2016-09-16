package info.glennengstrand.core;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import java.util.Properties;

public class KafkaPerformanceLogger extends MessageLogger<Long> {

	private final String host;
	private final String topic;
	private Producer<String, String> producer = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPerformanceLogger.class);

	private Producer<String, String> getProducer() {
		if (producer == null) {
			synchronized(LOGGER) {
				if (producer == null) {
					Properties config = new Properties();
					String brokers = host.concat(":9092");
					config.setProperty("bootstrap.servers", brokers);
					config.setProperty("metadata.broker.list", brokers);
					config.setProperty("zk.connect", host.concat(":2181"));
					config.setProperty("request.required.acks", "0");
					config.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
					config.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
					producer = new KafkaProducer<String, String>(config);
				}
			}
		}
		return producer;
	}
	
	@Override
	public void log(String entity, LogOperation operation, Long duration) {
		String msg = logRecord(entity, operation, duration);
		try {
			Producer<String, String> logger = getProducer();
			logger.send(new ProducerRecord<String, String>(topic, msg));
		} catch (Exception e) {
			LOGGER.warn("Cannot access Kafka: ", e);;
		}

	}
	
	public KafkaPerformanceLogger(String host, String topic) {
		this.host = host;
		this.topic = topic;
	}

}
