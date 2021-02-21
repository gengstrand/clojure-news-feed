(ns feed.daos.friends)

(defn fetch
  "fetch the friends for a participant"
  [id]
  [{:id id :from "/participant/1" :to "/participant/2"}])

(defn create
  "associate two participants as friends"
  [from to]
  {:id 0 :from from :to to})
