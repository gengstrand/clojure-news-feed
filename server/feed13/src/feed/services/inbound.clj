(ns feed.services.inbound
  (:require [feed.daos.inbound :as i]
  	    [feed.daos.cache :as c]))

(defn fetch
  "fetch the inbound news feed items for a participant"
  [id]
  (c/get id i/fetch))

(defn create
  "create an inbound news feed item for a participant"
  [from to occurred subject story]
  (i/create from to occurred subject story))
