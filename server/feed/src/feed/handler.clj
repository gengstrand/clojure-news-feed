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
         (log-performance m/participant-mbean (type (first result)) "get" (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/:id")
  (POST "/participant/new" [name] 
       (let [before (System/currentTimeMillis)
             result (c/logging-save (feed.core.Participant. 0 name) c/save-participant-to-cache)]
         (log-performance m/participant-mbean (type (first result)) "post" (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/new")
  (GET "/friends/:id" [id]
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "class feed.core.Friend" c/load-friends-from-cache c/save-friend-to-cache c/load-friends-from-db)]
         (log-performance m/friends-mbean (type (first result)) "get" (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/friends/:id")
  (POST "/friends/new" [from to] 
       (let [before (System/currentTimeMillis)
             result (c/logging-save (feed.core.Friend. 0 (parse-int from) (parse-int to)) c/save-friend-to-cache)]
         (log-performance m/friends-mbean (type (first result)) "post" (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/friends/new")
  (GET "/inbound/:id" [id]
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "class feed.core.Inbound" c/load-inbound-from-cache c/save-inbound-to-cache c/load-inbound-from-db)]
         (log-performance m/inbound-mbean (type (first result)) "get" (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/inbound/:id")
  (GET "/outbound/:id" [id]
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "class feed.core.Outbound" c/load-outbound-from-cache c/save-outbound-to-cache c/load-outbound-from-db)]
         (log-performance m/outbound-mbean (type (first result)) "get" (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/outbound/:id")
  (POST "/outbound/new" [from occurred subject story] 
       (let [before (System/currentTimeMillis)
             fromid (parse-int from)
             result (c/logging-save (feed.core.Outbound. fromid occurred subject story) c/save-outbound-to-cache)]
         (doseq [friend (c/logging-load fromid "class feed.core.Friend" c/load-friends-from-cache c/save-friend-to-cache c/load-friends-from-db)]
           (c/logging-save (feed.core.Inbound. (:to friend) fromid occurred subject story) c/save-inbound-to-cache))
         (log-performance m/outbound-mbean (type (first result)) "post" (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/outbound/new")
  (POST "/outbound/search" [terms]
       (let [before (System/currentTimeMillis)
             results (search/search terms)]
         (log-performance m/outbound-mbean "class feed.core.Outbound" "search" (- (System/currentTimeMillis) before))
         (str results)))
  (route/resources "/outbound/search")
        
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn -main
  "This is the main entry point for the application."
  [& args]
  (jetty/run-jetty app {:port 8080}))
