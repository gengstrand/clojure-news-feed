(ns feed.services.participant
  (:require [feed.daos.participant :as p]
            [feed.daos.cache :as c]))

(defn fetch
  "fetch a participant"
  [id]
  (c/get-entity "Participant" id p/fetch))

(defn create
  "create a participant"
  [name]
  (p/create name))
