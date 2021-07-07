(ns feed.controllers.inbound
  (:require [feed.services.inbound :as i]
            [feed.util :as u]
            [clojure.data.json :as json]))

(defn convert-inbound
  "change inbound by changing ids to hateoas uris"
  [inbound]
  {:from (u/embed-id (:from inbound))
   :to (u/embed-id (:to inbound))
   :occurred (:occurred inbound)
   :subject (:subject inbound)
   :story (:story inbound)})

(defn get-inbound
  "fetch inbound wrapper"
  [id]
  (try 
    {:status 200
     :body (clojure.string/replace (json/write-str (map #(convert-inbound %) (i/fetch (u/parse-int id)))) "\\" "")}
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