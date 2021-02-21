(ns feed.daos.participant)

(defn fetch
  "fetch a participant"
  [id]
  {:id id :name "test"})

(defn create
  "create a participant"
  [name]
  {:id 0 :name name})
