(ns feed.cache-redis)

(require '[taoensso.carmine :as redis :refer (wcar)])

(def cache-server-connection 
  {:pool {:max-active 8}
   :spec {:host "localhost"
   :port 6379
   :timeout 4000}})

(defmacro wcar* [& body] `(redis/wcar cache-server-connection ~@body))

(defn fetch-from-cache
  "attempt to fetch an item from the cache"
  [key]
  (wcar* (redis/get key)))

(defn save-to-cache
  "save a value to the cache by the given key"
  [key value]
  (wcar* (redis/set key value)))

(defn add-to-cache
  "add a value to a cached list of values"
  [key value]
  (wcar* (redis/append key value)))
