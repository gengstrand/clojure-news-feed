(ns load.integration
  (:require [clojure.tools.logging :as log]))

(require '[clojure.data.json :as json])
(require '[load.core :as core])
(require '[load.mysql :as mysql])
(require '[load.redis :as redis])
(require '[load.cassandra :as cassandra])
(require '[load.elastic :as elastic])

(def from-name "joe")
(def to-name "larry")
(def test-subject "test subject")
(def test-story "test story")

(defn report-error
  "print error message to console then exit"
  [error]
  (log/error error)
  (System/exit -1))

(defn perform-integration-test
  "perform basic use case and verify with underlying data stores"
  []
  (core/set-feed-host (System/getenv "FEED_HOST") (System/getenv "FEED_PORT"))
  (core/set-json-post (= (System/getenv "USE_JSON") "true"))
  (log/info "about to create participants")
  (let [from-id (core/test-create-participant from-name)
        to-id (core/test-create-participant to-name)
        extracted-from-name (mysql/load-participant-from-db from-id)
        extracted-to-name (mysql/load-participant-from-db to-id)]
    (log/info (str "extracted-from-name = " extracted-from-name))
    (log/info (str "extracted-to-name = " extracted-to-name))
    (if (not (and (= extracted-from-name from-name)
                  (= extracted-to-name to-name)))
      (report-error "error saving participants"))
    (log/info "about to fetch participants")
    (let [from-participant (:results (core/test-fetch-participant from-id))
          from-participant-name-via-service (get from-participant "name")
          to-participant (:results (core/test-fetch-participant to-id))
          to-participant-name-via-service (get to-participant "name")
          extracted-from-participant (json/read-str (redis/fetch-from-cache (redis/generate-participant-cache-key from-id)))
          from-participant-name-via-cache (get extracted-from-participant "name")
          extracted-to-participant (json/read-str (redis/fetch-from-cache (redis/generate-participant-cache-key to-id)))
          to-participant-name-via-cache (get extracted-to-participant "name")]
      (if (not (and (= from-participant-name-via-service from-participant-name-via-cache)
                    (= to-participant-name-via-service to-participant-name-via-cache)))
        (report-error "error fetching participants"))
      (log/info "about to friend participants")
      (core/test-create-friends from-id to-id)
      (log/info "about to broadcast socially")
      (core/test-create-outbound from-id (str "2014-01-0" (+ (rand-int 8) 1) "T19:25:51.490Z") test-subject test-story)
      (let [inbound-message (first (:results (core/test-fetch-inbound to-id)))
            inbound-subject-from-message (get inbound-message "subject")
            inbound-subject-from-cassandra (cassandra/load-inbound-subject-from-db to-id)]
        (log/info (str "fetch inbound = " inbound-message))
        (log/info (str "inbound subject from cassandra = " inbound-subject-from-cassandra))
        (log/info (str "inbound subject from feed = " inbound-subject-from-message))
        (if (not (and (= inbound-subject-from-message inbound-subject-from-cassandra)
                      (= inbound-subject-from-cassandra test-subject)))
          (report-error "error in social broadcast logic")))
      (if (not (= (first (elastic/search "test")) from-id))
        (report-error "error in keyword search")))))
