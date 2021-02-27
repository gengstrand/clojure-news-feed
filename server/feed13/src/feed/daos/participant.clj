(ns feed.daos.participant
  (:require [feed.daos.relational :as r]))

(defn get-name
  [handle id]
  (let [q (.createQuery handle "call FetchParticipant(:id)")
        bq (.bind q "id" id)
        rq (.mapToMap bq)
        rm (.first rq)]
        (.get rm "moniker")))

(defn insert-name
  [handle name]
  (let [q (.createQuery handle "call UpsertParticipant(:moniker)")
        bq (.bind q "moniker" name)
        rq (.mapTo bq Long)]
        (.first rq)))

(defn fetch
  "fetch a participant"
  [id]
  (let [h (.open @r/jdbi)]
    (try
      {:id id :name (get-name h id)}
      (finally (.close h)))))

(defn create
  "create a participant"
  [name]
  (let [h (.open @r/jdbi)]
    (try
      {:id (insert-name h name) :name name}
      (finally (.close h)))))

