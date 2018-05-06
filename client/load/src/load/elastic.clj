(ns load.elastic)

(require '[clojure.data.json :as json])
(require '[clj-http.client :as c])

(def elastic-host (System/getenv "ELASTIC_HOST"))
(def elastic-port (System/getenv "ELASTIC_PORT"))

(defn create-search-request
  "query elastic search for these keywords"
  [keywords]
  (str 
    "http://"
    elastic-host
    ":"
    (if (nil? elastic-port) "9200" elastic-port)
    "/_search?q="
    keywords))

(defn extract-sender 
  "extract the sender from this hit"
  [hit]
  (if (contains? hit "_source")
    (let [s (get hit "_source")]
      (if (contains? s "sender")
        (get s "sender")))))

(defn extract-search-results
  "extract the sender participant ids from the search response body"
  [body]
  (if (contains? body "hits")
    (let [outer (get body "hits")]
      (if (contains? outer "hits")
        (let [inner (get outer "hits")]
          (doall (map #(extract-sender %) inner)))))))

(defn search
  "search for these terms and return the matching list of senders"
  [terms]
  (let [uri (create-search-request terms)
        response (c/get uri {:accept :json})]
    (if 
      (<
        (:status response)
        300)
      (extract-search-results (json/read-str (:body response))))))

