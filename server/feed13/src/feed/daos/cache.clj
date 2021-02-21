(ns feed.daos.cache)

(defn get-entity
  "fetch an item from the cache"
  [key load]
  (load key))


