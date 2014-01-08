(ns feed.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [feed.core :as c]
            [feed.search :as search]
            [feed.metrics :as m]))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))
(def social-broadcast (agent nil))

(defroutes app-routes
  (GET "/participant/:id" [id] 
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "participant" c/load-participant-from-cache c/save-participant-to-cache c/load-participant-from-db)]
         (.logRun m/participant-mbean (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/:id")
  (POST "/participant/new" [name] 
       (let [before (System/currentTimeMillis)
             result (c/logging-save (feed.core.Participant. 0 name) c/save-participant-to-cache)]
         (.logRun m/participant-mbean (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/participant/new")
  (GET "/friends/:id" [id]
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "friends" c/load-friends-from-cache c/save-friend-to-cache c/load-friends-from-db)]
         (.logRun m/friends-mbean (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/friends/:id")
  (POST "/friends/new" [from to] 
       (let [before (System/currentTimeMillis)
             result (c/logging-save (feed.core.Friend. 0 (parse-int from) (parse-int to)) c/save-friend-to-cache)]
         (.logRun m/friends-mbean (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/friends/new")
  (GET "/inbound/:id" [id]
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "inbound" c/load-inbound-from-cache c/save-inbound-to-cache c/load-inbound-from-db)]
         (.logRun m/inbound-mbean (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/inbound/:id")
  (GET "/outbound/:id" [id]
       (let [before (System/currentTimeMillis)
             result (c/logging-load (parse-int id) "outbound" c/load-outbound-from-cache c/save-outbound-to-cache c/load-outbound-from-db)]
         (.logRun m/outbound-mbean (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/outbound/:id")
  (POST "/outbound/new" [from occurred subject story] 
       (let [before (System/currentTimeMillis)
             fromid (parse-int from)
             result (c/logging-save (feed.core.Outbound. fromid occurred subject story) c/save-outbound-to-cache)]
         (doseq [friend (c/logging-load fromid "friends" c/load-friends-from-cache c/save-friend-to-cache c/load-friends-from-db)]
           (c/logging-save (feed.core.Inbound. (:to friend) fromid occurred subject story) c/save-inbound-to-cache))
         (.logRun m/outbound-mbean (- (System/currentTimeMillis) before))
         (c/prepare-response-for-client result)))
  (route/resources "/outbound/new")
  (POST "/outbound/search" [terms]
       (let [before (System/currentTimeMillis)
             results (search/search terms)]
         (.logRun m/outbound-mbean (- (System/currentTimeMillis) before))
         (str results)))
  (route/resources "/outbound/search")
        
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
