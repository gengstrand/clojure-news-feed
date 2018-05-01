(ns load.cassandra)

(require '[qbits.alia :as alia])
(require '[clojure.string :as s])

(def cassandra-host (System/getenv "CASSANDRA_HOST"))
(def cassandra-port (System/getenv "CASSANDRA_PORT"))
(def cluster (if (nil? cassandra-host) nil (alia/cluster {:contact-points [cassandra-host] :port (if (nil? cassandra-port) 9042 (read-string cassandra-port)) :query-options {:consistency :one}})))
(def session (if (nil? cassandra-host) nil (alia/connect cluster "activity")))

(def load-inbound-from-db-command "select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc")
(def load-outbound-from-db-command "select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc")

(defn load-inbound-subject-from-db 
  "fetch the inbound activity for this participant from the db"
  [id]
  (let [results 
        (alia/execute session 
          (s/replace-first load-inbound-from-db-command "?" id))]
    (first (doall (map (fn [result] (:subject result)) results)))))
