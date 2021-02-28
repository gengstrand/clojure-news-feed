(ns feed.daos.cache
  (:require [feed.util :as u]
            [clojure.data.json :as json])
  (:import (redis.clients.jedis Jedis JedisPool JedisPoolConfig)))

(def pool (atom ""))

(defn connect
  "initialize connection to cache host"
  []
  (let [host (or (System/getenv "CACHE_HOST") "localhost")
        port (u/parse-int (or (System/getenv "CACHE_PORT") "6379"))
        timeout (u/parse-int (or (System/getenv "CACHE_TIMEOUT") "50000"))
        size (u/parse-int (or (System/getenv "CACHE_POOL") "10"))
        config (JedisPoolConfig.)]
        (.setMaxTotal config size)
        (.setBlockWhenExhausted config true)
        (swap! pool (fn [old] (JedisPool. config host port timeout)))))
        
(defn get-entity
  "fetch an item from the cache. if miss then load from db and update cache"
  [namespace key load]
  (let [p (.getResource @pool)]
        (try
          (let [nk (str namespace "::" key)
                rrv (.get p nk)
                prv (if (= rrv nil) (load key) (json/read-str rrv))]
                (if (= rrv nil)
                    (let [rv (json/write-str prv)]
                         (.set p nk rv)
                         (json/read-str rv))
                     prv))
          (catch Exception e (.println System/out (.getMessage e)))
          (finally (.close p)))))

(defn del
  "delete an entry from the cache"
  [namespace key]
  (let [p (.getResource @pool)]
        (try
          (.del p (str namespace "::" key))
          (catch Exception e (.println System/out (.getMessage e)))
          (finally (.close p)))))
