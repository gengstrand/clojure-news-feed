(ns feed.messaging-kafka)

(use 'clj-kafka.producer)
(require '[feed.settings :as prop])

(def p (producer {"metadata.broker.list" (str (:messaging-host prop/service-config) ":9092")
                  "serializer.class" "kafka.serializer.DefaultEncoder"
                  "partitioner.class" "kafka.producer.DefaultPartitioner"}))

(defn log
  "log a message to topic via kafka"
  [topic entity operation duration]
  (let [now (java.util.Calendar/getInstance)
        msg (str (.get now java.util.Calendar/YEAR)
                 "|"
                 (.get now java.util.Calendar/MONTH)
                 "|"
                 (.get now java.util.Calendar/DAY_OF_MONTH)
                 "|"
                 (.get now java.util.Calendar/HOUR_OF_DAY)
                 "|"
                 (.get now java.util.Calendar/MINUTE)
                 "|"
                 entity
                 "|"
                 operation
                 "|"
                 duration)]
  (try 
    (send-message p (message topic (.getBytes msg)))
    (catch Exception e 
      (println "messaging not available")
      nil))))
