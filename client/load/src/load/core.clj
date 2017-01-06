(ns load.core)

(require '[clojure.data.json :as json])
(require '[clj-http.client :as client])

(declare host)
(declare json-post)
(def port "8080")

(defn set-json-post 
  "switch to determine whether or not content type is json"
  [switch-value]
  (def json-post switch-value))

(defn set-feed-host
  "the ip address where the service is listening"
  [feed-host]
  (def host feed-host))

(defn service-url
  "generate the proper RESTful service url"
  [entity-name operation]
  (str
    "http://"
    host
    ":"
    port
    "/"
    entity-name
    "/"
    operation))

(defn test-create-entity-service-call
  "call the service to create an entity and return results and timing"
  [entity-name entity-params]
  (println entity-name)
  (println (json/write-str entity-params))
  (let [before (System/currentTimeMillis)
        response 
        (if json-post
          (client/post 
            (service-url entity-name "new") {:body (json/write-str entity-params) :content-type :json :accept :json})
          (client/post 
            (service-url entity-name "new") {:form-params entity-params}))]
    (if 
      (=
        (:status response)
        200)
      {:results (json/read-str (:body response))
       :duration (- (System/currentTimeMillis) before)})))

(defn test-create-entity-service-call-without-results
  "call the service to create an entity and return results and timing"
  [entity-name entity-params]
  (println entity-name)
  (println (json/write-str entity-params))
  (let [before (System/currentTimeMillis)
        response 
        (if json-post
          (client/post 
            (service-url entity-name "new") {:body (json/write-str entity-params) :content-type :json :accept :json})
          (client/post 
            (service-url entity-name "new") {:form-params entity-params}))]
    (if 
      (=
        (:status response)
        200)
      {:results (:body response)
       :duration (- (System/currentTimeMillis) before)})))

(defn test-search-entity-service-call
  "call the service to search for entities and return results and timing"
  [entity-name entity-params]
  (println entity-name)
  (println (json/write-str entity-params))
  (let [before (System/currentTimeMillis)
        response (client/post 
                   (service-url entity-name "search") {:query-params entity-params})]
    (if 
      (=
        (:status response)
        200)
      { :results (:body response) 
        :duration (- (System/currentTimeMillis) before)})))

(defn test-fetch-entity-service-call
  "call the service to fetch an instance of an entity and return results and timing"
  [entity-name entity-id]
  (let [before (System/currentTimeMillis)
        response (client/get 
                   (service-url entity-name entity-id))]
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
          	{:name name}))]
    (if 
      (vector? r)
      (get (first r) "id")
      (get r "id"))))

(defn test-fetch-participant
  "fetch a participant"
  [id]
  (test-fetch-entity-service-call "participant" id))

(defn test-create-friends
  "friend two participants"
  [from to]
  (test-create-entity-service-call
    "friends"
    {:from from :to to}))

(defn test-fetch-friends
  "fetch the friends of a participant"
  [id]
  (test-fetch-entity-service-call "friends" id))

(defn test-create-outbound
  "post an outbound activity to the senders friends"
  [from occurred subject story]
  (test-create-entity-service-call-without-results
    "outbound"
    {:from from :occurred occurred :subject subject :story story}))

(defn test-fetch-inbound
  "fetch the activity feed of a participant"
  [id]
  (test-fetch-entity-service-call "inbound" id))

(defn test-search
  "search for participants who have posted outbound activity containing these terms"
  [terms]
  (test-search-entity-service-call "outbound" {:keywords (str terms)}))

