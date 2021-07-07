(ns feed.controllers.participant
  (:require [feed.services.participant :as p]
            [feed.util :as u]
            [clojure.data.json :as json]))

(defn convert-participant
  "include link"
  [participant]
  {:id (get participant "id")
   :name (get participant "name")
   :link (u/embed-id (get participant "id"))})

(defn get-participant
  "fetch participant wrapper"
  [id]
  (try
    {:status 200
     :body (clojure.string/replace (json/write-str (convert-participant (p/fetch (u/parse-int id)))) "\\" "")}
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
