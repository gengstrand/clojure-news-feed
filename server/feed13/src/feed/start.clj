(ns feed.start
  (:require [com.appsflyer.donkey.core :refer [create-donkey create-server]]
          [com.appsflyer.donkey.server :refer [start]]
          [com.appsflyer.donkey.result :refer [on-success]]
          [com.appsflyer.donkey.middleware.params :refer [parse-query-params]]
          [clojure.data.json :as json]
          [feed.services.participant :as p]
          [feed.services.friends :as f]
          [feed.services.inbound :as i]
          [feed.services.outbound :as o]
          [feed.daos.relational :as r])
  (:gen-class :main true))

(defn parse-int [s]
  (Integer. (re-find  #"\d+" s )))

(defn extract-id [s]
  (Integer. (second (re-find #"/participant/(\d+)" s))))

(defn embed-id [id]
  (str "/participant/" id))
  
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

(defn convert-friend
  "change friend by changing ids to hateoas uris"
  [friend]
  {:id (:id friend)
   :from (embed-id (:from friend))
   :to (embed-id (:to friend))})
   
(defn get-friends
  "fetch friends wrapper"
  [id]
  (try 
    {:status 200
     :body (json/write-str (map #(convert-friend %) (f/fetch (parse-int id))))}
    (catch Exception e
      (.println System/out (.getMessage e))
      {:status 500
       :body (str "[{\"from\":" id ",\"to\":0,\"error\":\"" (.getMessage  e) "\"}]")})))

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

(defn convert-inbound
  "change inbound by changing ids to hateoas uris"
  [inbound]
  {:from (embed-id (:from inbound))
   :to (embed-id (:to inbound))
   :occurred (:occurred inbound)
   :subject (:subject inbound)
   :story (:story inbound)})

(defn get-inbound
  "fetch inbound wrapper"
  [id]
  (try 
    {:status 200
     :body (json/write-str (map #(convert-inbound %) (i/fetch (parse-int id))))}
    (catch Exception e
      (.println System/out (.getMessage e))
      {:status 500
       :body (str "[{\"to\":0,\"error\":\"" (.getMessage  e) "\"}]")})))

(defn handle-get-inbound
  "handle get inbound routing"
  []
  {:handler (fn [request] (get-inbound (get-in request [:path-params "id"])))
   :handler-mode :blocking
   :path "/participant/:id/inbound"
   :match-type :simple
   :methods [:get]
   :produces ["application/json"]})

(defn convert-outbound
  "change outbound by changing ids to hateoas uris"
  [outbound]
  {:from (embed-id (:from outbound))
   :occurred (:occurred outbound)
   :subject (:subject outbound)
   :story (:story outbound)})

(defn get-outbound
  "fetch outbound wrapper"
  [id]
  (try 
    {:status 200
     :body (json/write-str (map #(convert-outbound %) (o/fetch (parse-int id))))}
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
  [request respond raise]
  (let [body (apply str (map char (:body request)))
        parsed (json/read-str body)
        from (extract-id (get parsed "from"))
        occurred (get parsed "occurred")
        subject (get parsed "subject")
        story (get parsed "story")]
        (try
          (respond {:status 200
            :body (json/write-str (convert-outbound (o/create from occurred subject story)))})
          (catch Exception e
            (.println System/out (.getMessage e))
            (raise (.getMessage  e))))))

(defn handle-create-outbound
  "handle create outbound routing"
  []
  {:handler (fn [request respond raise] (create-outbound request respond raise))
   :handler-mode :non-blocking
   :path "/participant/:id/outbound"
   :match-type :simple
   :methods [:post]
   :consumes ["application/json"]
   :produces ["application/json"]})

(defn search-outbound
  "search outbound wrapper"
  [keywords]
  (try 
    {:status 200
     :body (json/write-str (map #(embed-id %) (o/search keywords)))}
    (catch Exception e
      (.println System/out (.getMessage e))
      {:status 500
       :body (str "{\"error\":\"" (.getMessage  e) "\"}")})))

(defn handle-search-outbound
  "handle search outbound routing"
  []
  {:handler (fn [request] (search-outbound (get-in request [:query-params "keywords"])))
   :handler-mode :blocking
   :path "/outbound"
   :match-type :simple
   :methods [:get]
   :middleware [(parse-query-params)]
   :produces ["application/json"]})

(defn -main
  "Application entry point"
  [& args]
  (r/connect)
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
                (handle-create-outbound)
                (handle-search-outbound)]})
    start
    (on-success (fn [_] (println "Server started listening on port 8080")))))