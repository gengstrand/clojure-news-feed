(ns feed.services.outbound
  (:require [feed.daos.outbound :as o]
            [feed.services.friends :as f]
            [feed.services.inbound :as i]))

(defn fetch
  "fetch the outbound news feed items for a participant"
  [id]
  (o/fetch id))

(defn create
  "create an outbound news feed item for a participant"
  [from occurred subject story]
  (future
    (try
      (doseq [friend (f/fetch from)]
        (i/create from (get friend "to") occurred subject story))
      (o/index from story)
      (o/create from occurred subject story)
      (catch Exception e (.println System/out (.getMessage e)))))
  {:from from :occurred occurred :subject subject :story story})

(defn search
  "search participants who posted this content"
  [keywords]
  (o/search keywords))
