(ns load.core)

(require '[clojure.data.json :as json])
(require '[clj-http.client :as client])
(require 'clojure.string)

(declare host)
(declare json-post)
(declare port)
(declare graphql)


(defn today
  "today's date as UTC formatted string"
  []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (.getTime (java.util.Calendar/getInstance))))

(defn set-json-post 
  "switch to determine whether or not content type is json"
  [switch-value]
  (def json-post switch-value))

(defn set-graphql
  "logic switch for making graphql calls"
  [switch-value]
  (def graphql switch-value))

(defn set-feed-host
  "the ip address and port where the service is listening"
  [feed-host feed-port]
  (def host feed-host)
  (def port feed-port))

(defn base-url
  "protocol host and port for the service"
  []
  (str
    "http://"
    host
    ":"
    port
    "/"))

(defn method-name
  "mutation method name for graphql calls"
  [entity-name]
  (str
    "create"
    (clojure.string/capitalize entity-name)))

(defn return-value
  "the return part of the graphql command"
  [entity-name]
  (if (= entity-name "friend")
    "{ to { id } }"
    (if (= entity-name "outbound")
      "{ from { id } }"
      "{ id }")))

(defn write-graphql-params 
  "format input entity as string"
  [entity-params]
  (clojure.string/join "," (map #(str (name %) ":\"" (% entity-params) "\"") (keys entity-params))))

(defn graphql-create 
  "generate graphql request body"
  [entity-name entity-params]
  {:query (str 
            "mutation { "
            (method-name entity-name)
            "(input: {"
            (write-graphql-params entity-params)
            "})"
            (return-value entity-name)
            "}")})

(defn graphql-search
  [entity-params]
  {:query (str "query { posters(keywords: \""
               (:keywords entity-params)
               "\") { id } }")})

(defn graphql-fetch
  "graphql command for fetching participants, friends, inbound, or outbound"
  [entity-name entity-id]
  {:query 
    (str 
      "query { participant(id: "
      entity-id
      ") "
      (if (= entity-name "participant")
        "{ name }"
        (if (= entity-name "friends")
          "{ friends { id, name } }"
          (if (= entity-name "inbound")
            "{ inbound { occurred, subject, story } }"
            "{ outbound { occurred, subject, story } }")))
      "}")})

(defn to-link
  "convert id to link"
  [participant-id]
  (str "/participant/" participant-id))

(defn service-url
  "generate the proper RESTful service url"
  [entity-name entity-id]
  (str
    (base-url)
      (if (= entity-name "participant")
        (if (nil? entity-id)
          "participant"
          (str "participant/" entity-id))
        (str "participant/" entity-id "/" entity-name))))

(defn test-create-entity-service-call
  "call the service to create an entity and return results and timing"
  [entity-name entity-id entity-params]
  (let [before (System/currentTimeMillis)
        response 
        (if graphql
          (client/post 
            (base-url) {:body (json/write-str (graphql-create entity-name entity-params)) :content-type :json :accept :json})
          (if json-post
            (client/post 
              (service-url entity-name entity-id) {:body (json/write-str entity-params) :content-type :json :accept :json})
            (client/post 
              (service-url entity-name entity-id) {:form-params entity-params})))]
    (if 
      (=
        (:status response)
        200)
      {:results (json/read-str (:body response))
       :duration (- (System/currentTimeMillis) before)}
      (throw (Exception. (str "status " (:status response) " from call to " entity-name))))))

(defn test-create-entity-service-call-without-results
  "call the service to create an entity and return results and timing"
  [entity-name entity-id entity-params]
  (let [before (System/currentTimeMillis)
        response 
        (if graphql
          (client/post 
            (base-url) {:body (json/write-str (graphql-create entity-name entity-params)) :content-type :json :accept :json})
          (if json-post
            (client/post 
              (service-url entity-name entity-id) {:body (json/write-str entity-params) :content-type :json :accept :json})
            (client/post 
              (service-url entity-name entity-id) {:form-params entity-params})))]
    (if 
      (=
        (:status response)
        200)
      {:results (:body response)
       :duration (- (System/currentTimeMillis) before)})))

(defn test-search-entity-service-call
  "call the service to search for entities and return results and timing"
  [entity-name entity-params]
  (let [before (System/currentTimeMillis)
        response 
        (if graphql
          (client/post 
            (base-url) {:body 
                        (json/write-str 
                          (graphql-search entity-params))
                        :content-type :json :accept :json})
          (client/get 
            (str (base-url) "outbound") {:query-params entity-params}))]
    (if 
      (=
        (:status response)
        200)
      { :results (json/read-str (:body response) )
        :duration (- (System/currentTimeMillis) before)})))

(defn test-fetch-entity-service-call
  "call the service to fetch an instance of an entity and return results and timing"
  [entity-name entity-id]
  (let [before (System/currentTimeMillis)
        response 
        (if graphql 
          (client/post 
            (base-url) {:body 
                        (json/write-str 
                          (graphql-fetch entity-name entity-id))
                        :content-type :json :accept :json})
          (client/get 
            (service-url entity-name entity-id)))]
    (if 
      (=
        (:status response)
        200)
      { :results (json/read-str (:body response))
        :duration (- (System/currentTimeMillis) before)})))

(defn test-create-participant
  "create a participant"
  [name]
  (let [r (:results
                (test-create-entity-service-call
                "participant"
                nil
                {:name name}))]
    (if 
      (vector? r)
      (get (first r) "id")
      (if graphql 
        (get (get (get r "data") "createParticipant") "id")
        (get r "id")))))

(defn test-fetch-participant
  "fetch a participant"
  [id]
  (let [retVal (test-fetch-entity-service-call "participant" id)]
    (if graphql
      {:results {:name (get (get (get (:results retVal) "data") "participant") "name")}}
       retVal)))

(defn test-create-friends
  "friend two participants"
  [from to]
  (test-create-entity-service-call
    (if graphql
      "friend"
      "friends")
    from
    (if graphql
      {:from_id from :to_id to}
      {:from (to-link from) :to (to-link to)})))

(defn test-fetch-friends
  "fetch the friends of a participant"
  [id]
  (test-fetch-entity-service-call "friends" id))

(defn test-create-outbound
  "post an outbound activity to the senders friends"
  [from occurred subject story]
  (test-create-entity-service-call-without-results
    "outbound"
    from
    (if graphql
      {:from_id from :occurred occurred :subject subject :story story}
      {:from (to-link from) :occurred occurred :subject subject :story story})))

(defn test-fetch-inbound
  "fetch the activity feed of a participant"
  [id]
  (let [retVal (test-fetch-entity-service-call "inbound" id)]
    (if graphql 
      {:results (get (get (get (:results retVal) "data") "participant") "inbound")}
       retVal)))

(defn test-search
  "search for participants who have posted outbound activity containing these terms"
  [terms]
  (test-search-entity-service-call "outbound" {:keywords (str terms)}))

