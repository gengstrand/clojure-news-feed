(ns feed.controllers.friends
  (:require [feed.services.friends :as f]
            [feed.util :as u]
            [clojure.data.json :as json]))

(defn convert-friend
  "change friend by changing ids to hateoas uris"
  [friend]
  {:id (get friend "id")
   :from (u/embed-id (get friend "from"))
   :to (u/embed-id (get friend "to"))})
   
(defn get-friends
  "fetch friends wrapper"
  [id]
  (try 
    {:status 200
     :body (clojure.string/replace (json/write-str (map #(convert-friend %) (f/fetch (u/parse-int id)))) "\\" "")}
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
        from (u/extract-id (get parsed "from"))
        to (u/extract-id (get parsed "to"))]
        (try
          {:status 200
           :body (json/write-str (convert-friend (f/create from to)))}
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
