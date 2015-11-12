(ns feed.core)

(require '[feed.rdbms :as db])
(require '[feed.cassandra :as cql])
(require '[feed.search :as search])
(require '[clojure.string :as s])
(require '[clojure.java.jdbc :as j])
(require '[qbits.alia :as alia])
(require '[feed.cache-redis :as c])
(require '[feed.messaging-kafka :as l])

(def msg-queue-name "feed")
(def ymd-date-formatter (java.text.SimpleDateFormat. "yyyy-MM-dd"))

(defn format-possible-date
  "if this is a date then format it to a string otherwise treat it like it has already been formatted as a string"
  [possible-date]
	(if (= (type possible-date) java.util.Date)
	  (.format ymd-date-formatter possible-date)
	  possible-date))

(defn logging-load
  "write through cache backed load from db with timing logged"
  [id type-name load-from-cache save-to-cache load-from-db]
  (let [result-from-cache (load-from-cache id)]
    (if (nil? result-from-cache)
      (let [before (System/currentTimeMillis)
            result-from-db (load-from-db id)]
        (l/log msg-queue-name type-name "load" (- (System/currentTimeMillis) before))
        (save-to-cache result-from-db)
        result-from-db)
      (load-string (str "[" result-from-cache "]")))))

(defprotocol ValueObject
  "serialize out object state for various purposes"
  (to-client [this])
  (to-db [this])
  (to-cache [this]))

(defrecord Participant [id moniker]
  ValueObject
  (to-client [this]
    (str "{\"id\": "
         (:id this)
         ", \"name\": \""
         (:moniker this)
         "\" }"))
  (to-db [this]
    (s/replace-first 
      db/save-participant-to-db-command "?" 
      (str "'" (:moniker this) "'")))
  (to-cache [this]
    (str "(feed.core.Participant. "
         (:id this)
         " \""
         (:moniker this)
         "\")")))

(defn generate-participant-cache-key
  "generate the key used for caching a participant"
  [id]
  (str "Participant::" id))

(defn load-participant-from-cache
  "fetch the participant from the cache"
  [id]
  (c/fetch-from-cache
    (generate-participant-cache-key id)))

(defn save-participant-to-cache
  "store the participant to the cache"
  [participant]
  (c/save-to-cache
    (generate-participant-cache-key (:id (first participant)))
    (reduce str (map #(str (to-cache %) " ") participant))))

(defn load-participant-from-db 
  "fetch this participant from the db"
  [id]
    (j/query (db/connection) [db/load-participant-from-db-command id]
      :row-fn #(Participant. id (:moniker %))))

(defn save-participant-to-db 
  "store this participant to the db"
  [participant]
    (j/query (db/connection) [(to-db participant)]
      :row-fn #(Participant. (:id %) (:moniker participant))))

(defrecord Friend [id from to]
  ValueObject
  (to-client [this]
    (str "{\"id\": "
         (:id this)
         ", \"from\": "
         (to-client (first (logging-load (:from this) "class feed.core.Participant" load-participant-from-cache save-participant-to-cache load-participant-from-db)))
         ", \"to\": "
         (to-client (first (logging-load (:to this) "class feed.core.Participant" load-participant-from-cache save-participant-to-cache load-participant-from-db)))
         " }"))
  (to-db [this]
    (-> db/save-friend-to-db-command
      (s/replace-first "?" (:from this))
      (s/replace-first "?" (:to this))))
  (to-cache [this]
    (str "(feed.core.Friend. "
         (:id this)
         " "
         (:from this)
         " "
         (:to this)
         ")")))

(defn generate-friends-cache-key
  "generate the key used for caching a list of friends"
  [from]
  (str "Friends::" from))

(defn load-friends-from-cache
  "fetch the friends list from the cache"
  [from]
  (c/fetch-from-cache
    (generate-friends-cache-key from)))

(defn save-friend-to-cache
  "store the a new friend to the list to the cache"
  [friend]
  (c/add-to-cache
    (generate-friends-cache-key (:from (first friend)))
    (reduce str (map #(str (to-cache %) " ") friend))))

(defn load-friends-from-db 
  "fetch the friends for this participant from the db"
  [id]
    (j/query (db/connection) [db/load-friends-from-db-command id]
      :row-fn #(Friend. (:friendsid %) id (:participantid %))))

(defn save-friend-to-db 
  "store this friend relationship to the db"
  [friend]
    (j/query (db/connection) [(to-db friend)]
      :row-fn #(Friend. (:id %) (:from friend) (:to friend))))

(defrecord Inbound [to from occurred subject story]
  ValueObject
  (to-client [this]
    (str "{\"to\": "
         (:to this)
         (if
           (number? (:from this))
           (str 
		         ", \"from\": "
		         (to-client (first (logging-load (:from this) "class feed.core.Participant" load-participant-from-cache save-participant-to-cache load-participant-from-db)))))
         ", \"occurred\": \""
         (format-possible-date (:occurred this))
         "\", \"subject\": \""
         (:subject this)
         "\", \"story\": \""
         (:story this)
         "\" }"))
  (to-db [this]
    (-> cql/save-inbound-to-db-command
      (s/replace-first "?" (:to this))
      (s/replace-first "?" (:from this))
      (s/replace-first "?" (str "'" (:subject this) "'"))
      (s/replace-first "?" (str "'" (:story this) "'"))))
  (to-cache [this]
    (str "(feed.core.Inbound. "
         (:to this)
         " "
         (:from this)
         " \""
         (format-possible-date (:occurred this))
         "\" \""
         (:subject this)
         "\" \""
         (:story this)
         "\")")))

(defn generate-inbound-cache-key
  "generate the key used for caching a users inbound activity"
  [to]
  (str "Inbound::" to))

(defn load-inbound-from-cache
  "fetch the users inbound activity from the cache"
  [to]
  (c/fetch-from-cache
    (generate-inbound-cache-key to)))

(defn save-inbound-to-cache
  "store the new activity to the users inbound activity in the cache"
  [inbound]
  (c/add-to-cache
    (generate-inbound-cache-key (:to (first inbound)))
    (reduce str (map #(str (to-cache %) " ") inbound))))

(defn load-inbound-from-db 
  "fetch the inbound activity for this participant from the db"
  [id]
  (let [results 
        (alia/execute cql/session 
          (s/replace-first cql/load-inbound-from-db-command "?" id))]
    (map (fn [result] (Inbound. id ((keyword "dateOf(occurred)") result) (:fromparticipantid result) (:subject result) (:story result))) results)))
  
(defn save-inbound-to-db 
  "store this inbound activity to the db"
  [inbound]
    (alia/execute cql/session (to-db inbound))
  (list inbound))

(defrecord Outbound [from occurred subject story]
  ValueObject
  (to-client [this]
    (str "{\"from\": "
         (:from this)
         ", \"occurred\": \""
         (format-possible-date (:occurred this))
         "\", \"subject\": \""
         (:subject this)
         "\", \"story\": \""
         (:story this)
         "\" }"))
  (to-db [this]
    (-> cql/save-outbound-to-db-command
      (s/replace-first "?" (:from this))
      (s/replace-first "?" (str "'" (:subject this) "'"))
      (s/replace-first "?" (str "'" (:story this) "'"))))
  (to-cache [this]
    (str "(feed.core.Outbound. "
         (:from this)
         " \""
         (format-possible-date (:occurred this))
         "\" \""
         (:subject this)
         "\" \""
         (:story this)
         "\")")))

(defn generate-outbound-cache-key
  "generate the key used for caching a users outbound activity"
  [from]
  (str "Outbound::" from))

(defn load-outbound-from-cache
  "fetch the users outbound activity from the cache"
  [from]
  (c/fetch-from-cache
    (generate-outbound-cache-key from)))

(defn save-outbound-to-cache
  "store the new activity to the users outbound activity in the cache"
  [outbound]
  (c/add-to-cache
    (generate-outbound-cache-key (:from (first outbound)))
    (reduce str (map #(str (to-cache %) " ") outbound))))

(defn load-outbound-from-db 
  "fetch the outbound activity from this participant from the db"
  [id]
  (let [results 
      (alia/execute cql/session 
        (s/replace-first cql/load-outbound-from-db-command "?" id))]
    (map (fn [result] (Outbound. id ((keyword "dateOf(occurred)") result) (:subject result) (:story result))) results)))

(defn save-outbound-to-db 
  "store this outbound activity to the db"
  [outbound]
    (alia/execute cql/session (to-db outbound))
  (search/index (:from outbound) (:story outbound))
  (list outbound))

(defn prepare-response-for-client
  "serialize out the collection of entities in a client friendly format"
  [entities]
  (str "["
       (reduce str (map #(str (to-client %) ", ") entities))
       "]"))

(defmulti save-to-db (fn [o] (type o)))

(defmethod save-to-db feed.core.Participant [o] (save-participant-to-db o))

(defmethod save-to-db feed.core.Friend [o] (save-friend-to-db o))

(defmethod save-to-db feed.core.Inbound [o] (save-inbound-to-db o))

(defmethod save-to-db feed.core.Outbound [o] (save-outbound-to-db o))

(defn logging-save
  "write through cache save to db with timing logged"
  [entity save-to-cache]
  (let [before (System/currentTimeMillis)
        result (save-to-db entity)]
    (l/log msg-queue-name (type entity) "store" (- (System/currentTimeMillis) before))
    (save-to-cache result)
    result))
