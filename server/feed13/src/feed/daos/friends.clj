(ns feed.daos.friends)

(defn fetch
  "fetch the friends for a participant"
  [id]
  [{:id id :from 1 :to 2}])

(defn create
  "associate two participants as friends"
  [from to]
  {:id 0 :from from :to to})
