(ns feed.cache-redis)

(require '[taoensso.carmine :as redis :refer (wcar)])
(require '[feed.settings :as prop])

(def cache-server-connection 
  {:pool {:max-active 8}
   :spec {:host (:cache-host prop/service-config)
   :port 6379
   :timeout 4000}})

(defmacro wcar* [& body] `(redis/wcar cache-server-connection ~@body))

(defn fetch-from-cache
  "attempt to fetch an item from the cache"
  [key]
  (try
    (wcar* (redis/get key))
    (catch Exception e 
      (println "cache not available")
      nil)))

(defn save-to-cache
  "save a value to the cache by the given key"
  [key value]
  (try 
    (wcar* (redis/set key value))
    (catch Exception e 
      (println "cache not available")
      nil)))

(defn add-to-cache
  "add a value to a cached list of values"
  [key value]
  (try 
    (wcar* (redis/append key value))
    (catch Exception e 
      (println "cache  not available")
      nil)))
