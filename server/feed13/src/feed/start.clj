(ns feed.start
  (:require [com.appsflyer.donkey.core :refer [create-donkey create-server]]
          [com.appsflyer.donkey.server :refer [start]]
          [com.appsflyer.donkey.result :refer [on-success]]
          [feed.controllers.participant :as p]
          [feed.controllers.friends :as f]
          [feed.controllers.inbound :as i]
          [feed.controllers.outbound :as o]
          [feed.daos.relational :as r])
  (:gen-class :main true))

(defn -main
  "Application entry point"
  [& args]
  (r/connect)
  (->
    (create-donkey)
    (create-server
      {:port   8080
       :routes [(p/handle-get-participant)
                (p/handle-create-participant)
                (f/handle-get-friends)
                (f/handle-create-friend)
                (i/handle-get-inbound)
                (o/handle-get-outbound)
                (o/handle-create-outbound)
                (o/handle-search-outbound)]})
    start
    (on-success (fn [_] (println "Server started listening on port 8080")))))