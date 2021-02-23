(ns feed.controllers.outbound
  (:require [feed.services.outbound :as o]
            [com.appsflyer.donkey.middleware.params :refer [parse-query-params]]
            [feed.util :as u]
            [clojure.data.json :as json]))

(defn convert-outbound
  "change outbound by changing ids to hateoas uris"
  [outbound]
  {:from (u/embed-id (:from outbound))
   :occurred (:occurred outbound)
   :subject (:subject outbound)
   :story (:story outbound)})

(defn get-outbound
  "fetch outbound wrapper"
  [id]
  (try 
    {:status 200
     :body (json/write-str (map #(convert-outbound %) (o/fetch (u/parse-int id))))}
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
        from (u/extract-id (get parsed "from"))
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
     :body (json/write-str (map #(u/embed-id %) (o/search keywords)))}
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