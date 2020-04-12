(ns feed.handler
  (:use [compojure.core])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [feed.core :as c]
            [feed.elastic :as search]
            [feed.metrics :as m]
            [feed.messaging-kafka :as l])
  (:gen-class :main true))

(def participant-entity "participant")
(def inbound-entity "inbound")
(def outbound-entity "outbound")
(def friends-entity "friends")
(def get-operation "get")
(def post-operation "post")
(def search-operation "search")

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn log-performance
  "log performance data both as a message and for access via JMX"
  [mbean entity operation duration]
  (.logRun mbean duration)
  (l/log c/msg-queue-name entity operation duration))

(defroutes app-routes
  (GET "/participant/:id" [id] 
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "class feed.core.Participant" c/load-participant-from-cache c/save-participant-to-cache c/load-participant-from-db)]
         (log-performance m/participant-mbean participant-entity get-operation (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/:id")
  (POST "/participant" [name] 
       (let [before (System/currentTimeMillis)
             result (c/logging-save (feed.core.Participant. 0 name) c/save-participant-to-cache)]
         (log-performance m/participant-mbean participant-entity post-operation (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant")
  (GET "/participant/:id/friends" [id]
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "class feed.core.Friend" c/load-friends-from-cache c/save-friend-to-cache c/load-friends-from-db)]
         (log-performance m/friends-mbean friends-entity get-operation (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/:id/friends")
  (POST "/participant/:id/friends" [id to] 
       (let [before (System/currentTimeMillis)
             result (c/logging-save (feed.core.Friend. 0 (parse-int id) (parse-int to)) c/save-friend-to-cache)]
         (log-performance m/friends-mbean friends-entity post-operation (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/:id/friends")
  (GET "/participant/:id/inbound" [id]
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "class feed.core.Inbound" c/load-inbound-from-cache c/save-inbound-to-cache c/load-inbound-from-db)]
         (log-performance m/inbound-mbean inbound-entity get-operation (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/:id/inbound")
  (GET "/participant/:id/outbound" [id]
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "class feed.core.Outbound" c/load-outbound-from-cache c/save-outbound-to-cache c/load-outbound-from-db)]
         (log-performance m/outbound-mbean outbound-entity get-operation (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/:id/outbound")
  (POST "/participant/:id/outbound" [id occurred subject story] 
       (let [before (System/currentTimeMillis)
             fromid (parse-int id)
             result (c/logging-save (feed.core.Outbound. fromid occurred subject story) c/save-outbound-to-cache)]
         (doseq [friend (c/logging-load fromid "class feed.core.Friend" c/load-friends-from-cache c/save-friend-to-cache c/load-friends-from-db)]
           (c/logging-save (feed.core.Inbound. (:to friend) fromid occurred subject story) c/save-inbound-to-cache))
         (log-performance m/outbound-mbean outbound-entity post-operation (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/:id/outbound")
  (GET "/outbound" {params :query-params}
       (let [before (System/currentTimeMillis)
             results (search/search (get params "keywords"))]
         (log-performance m/outbound-mbean outbound-entity search-operation (- (System/currentTimeMillis) before))
         (str results)))
  (route/resources "/outbound")
        
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn -main
  "This is the main entry point for the application."
  [& args]
  (jetty/run-jetty app {:port 8080}))
