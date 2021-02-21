(ns feed.services.inbound
  (:require [feed.daos.inbound :as i]))

(defn fetch
  "fetch the inbound news feed items for a participant"
  [id]
  (i/fetch id))

(defn create
  "create an inbound news feed item for a participant"
  [from to occurred subject story]
  (i/create from to occurred subject story))
