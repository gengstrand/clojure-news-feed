(ns perf5.core
  (:gen-class))

(require '[clojure.data.json :as json])
(require '[clj-http.client :as client])

(defn parse-long [s]
   (.longValue (Long. (re-find  #"\d+" s ))))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(def index-total-running 0)
(def index-time-running 0)
(def query-total-running 0)
(def query-time-running 0)

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
    
(defn report-elastic-search-results
  "extract relevant stats from elastic search request and print them to the console"
  [results index]
  (if (contains? results "indices")
    (let [indices (get results "indices")]
      (if (contains? indices index)
        (let [index-stats (get indices index)]
          (if (contains? index-stats "total")
            (let [total (get index-stats "total")]
              (if (contains? total "docs")
                (let [docs (get total "docs")]
                  (if (contains? total "indexing")
                    (let [indexing (get total "indexing")]
                      (if (contains? total "search")
                        (let [search (get total "search")
                              results (refresh-stats (get indexing "index_total") (get indexing "index_time_in_millis") (get search "query_total") (get search "query_time_in_millis"))
                              avg-index-time (if (= (:index-total results) 0) 0 (quot (:index-time results) (:index-total results)))
                              avg-query-time (if (= (:query-total results) 0) 0 (quot (:query-time results) (:query-total results)))]
                          (println 
                            (str
                              (get docs "count")
                              ","
                              (:index-total results)
                              ","
                              avg-index-time
                              ","
                              (:query-total results)
                              ","
                              avg-query-time)))))))))))))))

(defn process-solr
  ""
  [host port index])

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
