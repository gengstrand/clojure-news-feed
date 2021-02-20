(ns feed.start
  (:require [com.appsflyer.donkey.core :refer [create-donkey create-server]]
          [com.appsflyer.donkey.server :refer [start]]
          [com.appsflyer.donkey.result :refer [on-success]]
          [compojure.core :refer :all]
          [compojure.route :as route]
          [compojure.handler :as handler]
          [clojure.data.json :as json]
          [feed.services.participant :as p]
          [feed.services.friends :as f]
          [feed.services.inbound :as i]
          [feed.services.outbound :as o])
  (:gen-class :main true))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn extract-id [id]
   (Integer. (re-find #"/participant/(\d+)" id)))

(defn get-participant
  "fetch participant wrapper"
  [id]
  (try
    {:status 200
     :body (json/write-str (p/fetch (parse-int id)))}
    (catch Exception e
      (.println System/out (.getMessage e))
      {:status 500
       :body (str "{\"id\":0,\"name\":\"" (.getMessage e) "\"}")})))

(defn handle-get-participant
  "handler for get participant route"
  []
  {:handler (fn [request] (get-participant (get-in request [:path-params "id"])))
   :handler-mode :blocking
   :path "/participant/:id"
   :match-type :simple
   :methods [:get]
   :produces ["application/json"]})

(defn create-participant
  "create participant wrapper"
  [request]
  (let [body (apply str (map char (:body request)))
        parsed (json/read-str body)
        name (get parsed "name")]
        (try
          {:status 200
           :body (json/write-str (p/create name))}
          (catch Exception e
            (.println System/out (.getMessage e))
            {:status 500
             :body (str "{\"id\":0,\"name\":\"" (.getMessage e) "\"}")}))))

(defn handle-create-participant
  "handler for create participant route"
  []
  {:handler (fn [request] (create-participant request))
   :handler-mode :blocking
   :path "/participant"
   :match-type :simple
   :methods [:post]
   :consumes ["application/json"]
   :produces ["application/json"]})

(defn get-friends
  "fetch friends wrapper"
  [id]
  (try 
    {:status 200
     :body (json/write-str (f/fetch (parse-int id)))}
    (catch Exception e
      (.println System/out (.getMessage e))
      {:status 500
       :body (str "{\"from\":" id ",\"to\":0,\"error\":\"" (.getMessage  e) "\"}")})))

(defn handle-get-friends
  "handle get friends routing"
  []
  {:handler (fn [request] (get-friends (get-in request [:path-params "id"])))
   :handler-mode :blocking
   :path "/participant/:id/friends"
   :match-type :simple
   :methods [:get]
   :produces ["application/json"]})

(defn create-friend
  "create friend wrapper"
  [request]
  (let [body (apply str (map char (:body request)))
        parsed (json/read-str body)
        from (extract-id (get parsed "from"))
        to (extract-id (get parsed "to"))]
        (try
          {:status 200
           :body (json/write-str (f/create from to))}
          (catch Exception e
            (.println System/out (.getMessage e))
            {:status 500
             :body (str "{\"from\":" from ",\"to\":" to ",\"error\":\"" (.getMessage  e) "\"}")}))))

(defn handle-create-friend
  "handle create friend routing"
  []
  {:handler (fn [request] (create-friend request))
   :handler-mode :blocking
   :path "/participant/:id/friends"
   :match-type :simple
   :methods [:post]
   :consumes ["application/json"]
   :produces ["application/json"]})

(defn get-inbound
  "fetch inbound wrapper"
  [id]
  (try 
    {:status 200
     :body (json/write-str (i/fetch (parse-int id)))}
    (catch Exception e
      (.println System/out (.getMessage e))
      {:status 500
       :body (str "{\"to\":0,\"error\":\"" (.getMessage  e) "\"}")})))

(defn handle-get-inbound
  "handle get inbound routing"
  []
  {:handler (fn [request] (get-inbound (get-in request [:path-params "id"])))
   :handler-mode :blocking
   :path "/participant/:id/inbound"
   :match-type :simple
   :methods [:get]
   :produces ["application/json"]})

(defn get-outbound
  "fetch outbound wrapper"
  [id]
  (try 
    {:status 200
     :body (json/write-str (o/fetch (parse-int id)))}
    (catch Exception e
      (.println System/out (.getMessage e))
      {:status 500
       :body (str "{\"from\":" id ",\"error\":\"" (.getMessage  e) "\"}")})))

(defn handle-get-outbound
  "handle get outbound routing"
  []
  {:handler (fn [request] (get-outbound (get-in request [:path-params "id"])))
   :handler-mode :blocking
   :path "/participant/:id/outbound"
   :match-type :simple
   :methods [:get]
   :produces ["application/json"]})

(defn create-outbound
  "create outbound wrapper"
  [request]
  (let [body (apply str (map char (:body request)))
        parsed (json/read-str body)
        from (extract-id (get parsed "from"))
        occurred (get parsed "occurred")
        subject (get parsed "subject")
        story (get parsed "story")]
        (try
          {:status 200
           :body (json/write-str (o/create from occurred subject story))}
          (catch Exception e
            (.println System/out (.getMessage e))
            {:status 500
             :body (str "{\"from\":" from ",\"error\":\"" (.getMessage  e) "\"}")}))))

(defn handle-create-outbound
  "handle create outbound routing"
  []
  {:handler (fn [request] (create-friend request))
   :handler-mode :blocking
   :path "/participant/:id/outbound"
   :match-type :simple
   :methods [:post]
   :consumes ["application/json"]
   :produces ["application/json"]})

(defn -main
  "Application entry point"
  [& args]
  (->
    (create-donkey)
    (create-server
      {:port   8080
       :routes [(handle-get-participant)
                (handle-create-participant)
                (handle-get-friends)
                (handle-create-friend)
                (handle-get-inbound)
                (handle-get-outbound)
                (handle-create-outbound)]})
    start
    (on-success (fn [_] (println "Server started listening on port 8080")))))