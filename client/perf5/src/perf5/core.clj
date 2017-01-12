(ns perf5.core
  (:gen-class))

(require '[clojure.data.json :as json])
(require '[clj-http.client :as client])
(require '(clojure [zip :as zip]))

(defn parse-long [s]
   (.longValue (Long. (re-find  #"\d+" s ))))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(def index-total-running 0)
(def index-time-running 0)
(def query-total-running 0)
(def query-time-running 0)
(def total-docs 0)

(defn extract-num-docs
  "get the total number of docs from the solr stats"
  [core]
  (let [searcher (get core "searcher")
        searcher-stats (get searcher "stats")]
    (def total-docs (get searcher-stats "numDocs"))))

(defn refresh-stats 
  "maintain incremental statistics data"
  [index-total index-time query-total query-time]
  (let [retVal {
                :index-total (- index-total index-total-running)
                :index-time (- index-time index-time-running)
                :query-total (- query-total query-total-running)
                :query-time (- query-time query-time-running)}]
    (def index-total-running index-total)
    (def index-time-running index-time)
    (def query-total-running query-total)
    (def query-time-running query-time)
    retVal))

(defn following 
  "call back with the item following the item that matches the tag"
  [vector tag callback]
  (loop [item (zip/vector-zip vector)]
    (if (not (zip/end? item))
      (let [next-item (zip/next item)]
        (if (= (zip/node next-item) tag)
          (callback (zip/node (zip/next next-item)))
          (recur next-item))))))

(defn report-stats 
  "report the stats to the console"
  [results total-docs]
  (let [avg-index-time (if (= (:index-total results) 0) 0 (quot (:index-time results) (:index-total results)))
        avg-query-time (if (= (:query-total results) 0) 0 (quot (:query-time results) (:query-total results)))]
        (println 
          (str
            total-docs
            ","
            (:index-total results)
            ","
            avg-index-time
            ","
            (:query-total results)
            ","
            avg-query-time))))

(defn report-solr-stats-handler-data
  "extract relevant stats from solr request"
  [query-handler]
  (let [update (get query-handler "/update")
        update-stats (get update "stats")
        standard (get query-handler "standard")
        standard-stats (get standard "stats")
        results (refresh-stats (get update-stats "requests") (get update-stats "totalTime") (get standard-stats "requests") (get standard-stats "totalTime"))]
    (report-stats results total-docs)))

(defn report-solr-results
  "process the response to the request for solr stats"
  [results]
  (let [solr-mbeans (get results "solr-mbeans")]
    (following solr-mbeans "CORE" extract-num-docs)
    (following solr-mbeans "QUERYHANDLER" report-solr-stats-handler-data)))

(defn report-elastic-search-results
  "extract relevant stats from elastic search request"
  [results index]
  (let [indices (get results "indices")
        index-stats (get indices index)
        total (get index-stats "total")
        docs (get total "docs")
        indexing (get total "indexing")
        search (get total "search")
        results (refresh-stats (get indexing "index_total") (get indexing "index_time_in_millis") (get search "query_total") (get search "query_time_in_millis"))]
        (report-stats results (get docs "count"))))

(defn process-solr
  "fetch stats from solr"
  [host port index]
  (let [url 
        (str "http://"
             host
             ":"
             port
             "/"
             index
             "/admin/mbeans?stats=true&wt=json")
        response (client/get url)]
    (if 
      (< (:status response 300))
      (report-solr-results (json/read-str (:body response)))
      (println response))))

(defn process-elastic-search
  "fetch stats from elastic search"
  [host port index]
  (let [url 
        (str "http://"
             host
             ":"
             port
             "/"
             index
             "/_stats/docs,indexing,search")
        response (client/get url)]
    (if 
      (< (:status response 300))
      (report-elastic-search-results (json/read-str (:body response)) index))))

(defn -main
  "monitor search appliance stats"
  [& args]
  (if 
    (>= (count args) 6)
    (let [wait-interval (parse-long (nth args 5))]
      (println "docs,index_total,avg_index_time,search_total,avg_search_time")
      (doseq [i (range (parse-int (nth args 4)))]
        (if 
          (= (nth args 0) "solr")
          (process-solr (nth args 1) (nth args 2) (nth args 3))
          (process-elastic-search (nth args 1) (nth args 2) (nth args 3)))
        (java.lang.Thread/sleep wait-interval)))
      (println "usage: java -jar perf5-0.1.0-standalone.jar type host port index times interval")))
