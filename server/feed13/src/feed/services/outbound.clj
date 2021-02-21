(ns feed.services.outbound
  (:require [feed.daos.outbound :as o]
  	    [feed.daos.cache :as c]))

(defn fetch
  "fetch the outbound news feed items for a participant"
  [id]
  (c/get id o/fetch))

(defn create
  "create an outbound news feed item for a participant"
  [from occurred subject story]
  (o/create from occurred subject story))

(defn search
  "search participants who posted this content"
  [keywords]
  ["/participant/1"])
