(ns feed.daos.friends
  (:require [feed.daos.relational :as r]))

(defn to-friend
  "convert from db row to api response"
  [item]
  {:id (get item "friendsid")
   :from (get item "fromparticipantid")
   :to (get item "toparticipantid")})

(defn fetch
  "fetch the friends for a participant"
  [id]
  (let [h (.open @r/jdbi)]
    (try
      (let [q (.createQuery h "call FetchFriends(:id)")
            bq (.bind q "id" id)
            rq (.mapToMap bq)]
            (map #(to-friend %) (.list rq)))
      (finally (.close h)))))

(defn create
  "associate two participants as friends"
  [from to]
  (let [h (.open @r/jdbi)]
    (try
      (let [q (.createQuery h "call UpsertFriends(:from, :to)")
            bq1 (.bind q "from" from)
            bq2 (.bind bq1 "to" to)
            rq (.mapTo bq2 Long)]
            {:id (.first rq) :from from :to to})
      (finally (.close h)))))

