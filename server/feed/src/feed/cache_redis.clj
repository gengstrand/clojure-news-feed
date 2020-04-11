(ns feed.cache-redis)

(use 'clojure.string)
(require '[taoensso.carmine :as redis :refer (wcar)])
(require '[feed.settings :as prop])

(def cache-server-connection 
  (if (nil? prop/service-config)
    nil
	  {:pool {}
	   :spec {:host (:cache-host prop/service-config)
	   :port 6379
	   :timeout 4000}}))

(defmacro wcar* [& body] `(redis/wcar cache-server-connection ~@body))

(defn valid?
  "check for well founded-ness"
  [value]
  (and (not (nil? value)) (not (= (trim value) ""))))

(defn fetch-from-cache
  "attempt to fetch an item from the cache"
  [key]
  (try
    (wcar* (redis/get key))
    (catch Exception e 
      (println (str "cannot fetch from cache: " (.getLocalizedMessage e)))
      nil)))

(defn save-to-cache
  "save a value to the cache by the given key"
  [key value]
  (if (valid? value)
    (try 
      (wcar* (redis/set key value))
      (catch Exception e 
        (println (str "cannot save to cache: " (.getLocalizedMessage e)))
        nil))))

(defn add-to-cache
  "add a value to a cached list of values"
  [key value]
  (if (valid? value)
    (try 
      (wcar* (redis/append key value))
      (catch Exception e 
        (println (str "cannot add to cache: " (.getLocalizedMessage e)))
        nil))))
