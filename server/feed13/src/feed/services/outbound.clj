(ns feed.services.outbound
  (:require [feed.daos.outbound :as o]))

(defn fetch
  "fetch the outbound news feed items for a participant"
  [id]
  (o/fetch id))

(defn create
  "create an outbound news feed item for a participant"
  [from occurred subject story]
  (o/create from occurred subject story))

(defn search
  "search participants who posted this content"
  [keywords]
  (o/search keywords))
