(ns feed.elastic
  (:import java.util.UUID))

(require '[feed.settings :as prop])
(require '[clojure.data.json :as json])
(require '[clj-http.client :as c])

(defn create-search-request
  "query elastic search for these keywords"
  [keywords]
  (str 
    (:search-host prop/service-config)
    "/_search?q="
    keywords))

(defn create-index-request
  "add a document to the index"
  [id key]
  (str 
    (:search-host prop/service-config)
    "/"
    id
    "-"
    key))

(defn create-entity
  "create a document to be sent to elastic search"
  [id key story]
  {:id key
   :sender id
   :story story})

(defn extract-sender 
  "extract the sender from this hit"
  [hit]
  (if (contains? hit "_source")
    (let [s (get hit "_source")]
      (if (contains? s "sender")
        (str "/participant/" (get s "sender"))))))

(defn extract-search-results
  "extract the sender participant ids from the search response body"
  [body]
  (if (contains? body "hits")
    (let [outer (get body "hits")]
      (if (contains? outer "hits")
        (let [inner (get outer "hits")]
          (doall (map #(extract-sender %) inner)))))))

(defn index
  "add this story to the search index"
  [from story]
  (let [key (.toString (UUID/randomUUID))
        uri (create-index-request from key)
        se (create-entity from key story)
        response (c/put uri {:body (json/write-str se) :content-type :json :accept :json})]
    (if 
      (<
        (:status response)
        300)
      (:body response))))

(defn search
  "search for these terms and return the matching list of senders"
  [terms]
  (let [uri (create-search-request terms)
        response (c/get uri {:accept :json})]
    (if 
      (<
        (:status response)
        300)
      (json/write-str (extract-search-results (json/read-str (:body response)))))))
