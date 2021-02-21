(ns feed.daos.participant
  (:require [feed.daos.relational :as r]))

(defn get-name
  [handle id]
  (let [q (.createQuery handle "select Moniker from Participant where ParticipantID = :id")
        bq (.bind q "id" id)
        rq (.mapToMap bq)
        rm (.first rq)]
        (.get rm "moniker")))

(defn insert-name
  [handle name]
  (let [q (.createUpdate handle "insert into Participant (Moniker) values (:name)")
        bq (.bind q "name" name)]
        (.execute bq)))

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
      (insert-name h name)
      {:id 0 :name name}
      (finally (.close h)))))


