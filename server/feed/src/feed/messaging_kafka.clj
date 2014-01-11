(ns feed.messaging-kafka)

(use 'clj-kafka.producer)
(require '[feed.settings :as prop])

(def p (producer {"metadata.broker.list" (str prop/messaging-host ":9092")
                  "serializer.class" "kafka.serializer.DefaultEncoder"
                  "partitioner.class" "kafka.producer.DefaultPartitioner"}))
(defn log
  "log a message to topic via kafka"
  [topic msg]
  (send-message p (message topic (.getBytes msg))))
