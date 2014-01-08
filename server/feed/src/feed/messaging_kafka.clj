(ns feed.messaging-kafka)

(use 'clj-kafka.producer)

; (brokers {"zookeeper.connect" "127.0.0.1:2181"})

(def p (producer {"metadata.broker.list" "localhost:9092"
                  "serializer.class" "kafka.serializer.DefaultEncoder"
                  "partitioner.class" "kafka.producer.DefaultPartitioner"}))
(defn log
  "log a message to topic via kafka"
  [topic msg]
  (send-message p (message topic (.getBytes msg))))
