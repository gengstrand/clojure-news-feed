(ns feed.cache-memcached
  (:import net.spy.memcached.MemcachedClient)
  (:import java.net.InetSocketAddress))

(require '[feed.settings :as prop])

(def ttl 300)
(def client
  (if (nil? prop/service-config)
    nil
    (let [socket (InetSocketAddress. (:cache-host prop/service-config) 11211)
          retVal (MemcachedClient. socket)]
      retVal)))

(defn fetch-from-cache
  "attempt to fetch an item from the cache"
  [key]
  (try
    (.get client key)
    (catch Exception e 
      (println (str "cannot fetch from cache: " (.getLocalizedMessage e)))
      nil)))

(defn save-to-cache
  "save a value to the cache by the given key"
  [key value]
  (try 
    (.set client key ttl value)
    (catch Exception e 
      (println (str "cannot save to cache: " (.getLocalizedMessage e)))
      nil)))

(defn add-to-cache
  "add a value to a cached list of values"
  [key value]
  (try 
    (.set client key ttl value)
    (catch Exception e 
      (println (str "cannot add to cache: " (.getLocalizedMessage e)))
      nil)))