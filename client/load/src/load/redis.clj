(ns load.redis
  (:require [clojure.tools.logging :as log]))

(require '[taoensso.carmine :as redis :refer (wcar)])

(def redis-host (System/getenv "REDIS_HOST"))
(def redis-port (System/getenv "REDIS_PORT"))

(def cache-server-connection 
  (if (nil? redis-host)
    nil
	  {:pool {}
	   :spec {:host redis-host
	   :port (if (nil? redis-port) 6379 (read-string redis-port))
	   :timeout 4000}}))

(defmacro wcar* [& body] `(redis/wcar cache-server-connection ~@body))

(defn generate-participant-cache-key
  "generate the key used for caching a participant"
  [id]
  (str "Participant::" id))

(defn fetch-from-cache
  "attempt to fetch an item from the cache"
  [key]
  (let [retVal (try
                 (wcar* (redis/get key))
                 (catch Exception e 
                   (log/error (str "cannot fetch from cache: " (.getLocalizedMessage e)))
                   nil))]
    (log/info (str key " = " retVal))
    retVal))
