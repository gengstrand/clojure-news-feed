(ns load.integration
  (:require [clojure.tools.logging :as log]))

(require '[clojure.data.json :as json])
(require '[load.core :as core])
(require '[load.mysql :as mysql])
(require '[load.redis :as redis])
(require '[load.cassandra :as cassandra])
(require '[load.elastic :as elastic])
(require '[clojure.string :as s])

(def from-name "joe")
(def to-name "larry")
(def test-subject "test subject")
(def test-story "test story")

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn starts-with? 
  "determines whether or not a string value begins with another value"
  [look-in look-for]
  (if (> (.length look-in) (.length look-for))
    (= (subs look-in 0 (.length look-for)) look-for)
    false))

(defn report-error
  "print error message to console then exit"
  [error]
  (log/error error)
  (System/exit -1))

(defn verify-two-participants
  "verify participant creation in mysql and redis"
  [from-id to-id]
  (let [extracted-from-name (mysql/load-participant-from-db from-id)
        extracted-to-name (mysql/load-participant-from-db to-id)]
      (log/info (str "extracted-from-name = " extracted-from-name))
      (log/info (str "extracted-to-name = " extracted-to-name))
      (if (not (and (= extracted-from-name from-name)
                    (= extracted-to-name to-name)))
        (report-error "error saving participants"))
      (log/info "about to fetch participants")
      (let [from-participant (:results (core/test-fetch-participant from-id))
            from-participant-name-via-service (get from-participant "name")
            from-participant-link-via-service (get from-participant "link")
            to-participant (:results (core/test-fetch-participant to-id))
            to-participant-name-via-service (get to-participant "name")
            extracted-from-participant-cache-value (redis/fetch-from-cache (redis/generate-participant-cache-key from-id))
            extracted-to-participant-cache-value (redis/fetch-from-cache (redis/generate-participant-cache-key to-id))]
        (if (or (nil? extracted-from-participant-cache-value) (nil? extracted-to-participant-cache-value))
          (report-error "fetching participants does not load the cache"))
        (if (not (= from-participant-link-via-service (str "/participant/" from-id)))
          (report-error "fetching participants does not return correct link"))
        (if (not (or (starts-with? extracted-from-participant-cache-value "(") (starts-with? extracted-to-participant-cache-value "(")))
          (let [extracted-from-participant (json/read-str extracted-from-participant-cache-value)
                 from-participant-name-via-cache (get extracted-from-participant "name")
                 extracted-to-participant (json/read-str extracted-to-participant-cache-value)
                 to-participant-name-via-cache (get extracted-to-participant "name")]
            (if (not (and (= from-participant-name-via-service from-participant-name-via-cache)
                          (= to-participant-name-via-service to-participant-name-via-cache)))
              (let [from-participant-name-via-cache-for-feed2 (get (first extracted-from-participant) "Moniker")
                 to-participant-name-via-cache-for-feed2 (get (first extracted-to-participant) "Moniker")]
                (if (not (and (= from-participant-name-via-service from-participant-name-via-cache-for-feed2)
                              (= to-participant-name-via-service to-participant-name-via-cache-for-feed2)))
                  (report-error "error fetching participants")))))))))

(defn test-verify-social-broadcast
  "post outbound and verify results"
  [from-id to-id]
  (log/info "about to broadcast socially")
  (let [now (core/today)]
    (log/info (str "create outbound with occurred: " now))
    (core/test-create-outbound from-id now test-subject test-story)
    (Thread/sleep 10000)
    (let [inbound-message (first (:results (core/test-fetch-inbound to-id)))
        inbound-subject-from-message (get inbound-message "subject")
        inbound-occurred-from-message (get inbound-message "occurred")
        inbound-subject-from-cassandra (cassandra/load-inbound-subject-from-db to-id)]
      (log/info (str "fetch inbound = " inbound-message))
      (log/info (str "inbound subject from cassandra = " inbound-subject-from-cassandra))
      (log/info (str "inbound subject from feed = " inbound-subject-from-message))
      (if (not (and (= inbound-subject-from-message inbound-subject-from-cassandra)
                  (or (= inbound-subject-from-cassandra test-subject)
                      (= inbound-subject-from-cassandra (s/replace test-subject " " "+")))))
        (report-error "error in social broadcast logic"))
      (if (not (= inbound-occurred-from-message now))
        (report-error "error in occurred logic part of social broadcast"))))
  (Thread/sleep 10000)
  (let [from-sender (str "/participant/" (.toString from-id))
       search-results (:results (core/test-search "test"))]
    (if (not (>= (count (doall (filter (fn [sender] (or (= sender from-id) (= sender from-sender))) (elastic/search "test")))) 1))
      (report-error (str "Error in keyword search. Cannot find sender " from-id " in " (json/write-str (elastic/search "test")))))
    (if (not (>= (count (doall (filter (fn [sender] (= from-sender sender)) search-results))) 1))
      (report-error (str "Error in keyword search. Cannot find sender " from-sender " in " search-results)))))

(defn perform-integration-test
  "perform basic use case and verify with underlying data stores"
  []
  (core/set-feed-host (System/getenv "FEED_HOST") (System/getenv "FEED_PORT"))
  (core/set-json-post (= (System/getenv "USE_JSON") "true"))
  (core/set-graphql (= (System/getenv "USE_GRAPHQL") "true"))
  (log/info "about to create participants")
  (try 
    (let [from-id (core/test-create-participant from-name)
          to-id (core/test-create-participant to-name)]
      (verify-two-participants from-id to-id)
      (log/info "about to friend participants")
      (core/test-create-friends from-id to-id)
      (test-verify-social-broadcast from-id to-id))
    (log/info "tests passed")
    (System/exit 0)
(catch Exception e
  (report-error (str "unexpected error: " (.getLocalizedMessage e))))))
