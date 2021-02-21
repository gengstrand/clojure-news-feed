(ns feed.daos.cache)

(defn get
  "fetch an item from the cache"
  [key load]
  (load key))

(defn del
  "delete an item from the cache"
  [key])
