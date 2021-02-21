(ns feed.services.friends
  (:require [feed.daos.friends :as f]
            [feed.daos.cache :as c]))

(defn fetch
  "fetch the friends for a participant"
  [id]
  (c/get-entity id f/fetch))

(defn create
  "associate two participants as friends"
  [from to]
  (f/create from to))
