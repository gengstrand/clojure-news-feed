(ns etl.core)

(use 'clojure.java.io)
(require '[clojure.string :as s])
(require '[clojure.java.jdbc :as db])

(def entities ["Friend" "Inbound" "Outbound" "Participant"])

(def activities ["get" "load" "post" "search" "store"])

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn build-insert-time-statement 
  ""
  [time-stamp]
  (str 
    "{ call InsertTimeData("
    (:year time-stamp)
    ", "
    (:month time-stamp)
    ", "
    (:day time-stamp)
    ", "
    (:hour time-stamp)
    ", "
    (:minute time-stamp)
    ") }"))

(defn build-insert-fact-statement 
  ""
  [metric time-id]
  (str 
    "{ call InsertFactData("
    (:throughput metric) 
    ", "
    (:mode metric) 
    ", "
    (:vigintile metric) 
    ", "
    (+ (.indexOf activities (:activity metric)) 1)
    ", "
    (+ (.indexOf entities (:entity metric)) 1)
    ", "
    time-id
    ") }"))

(defn insert-time
  ""
  [time-stamp db-connection]
  (db/with-connection db-connection
    (db/with-query-results rs [(build-insert-time-statement time-stamp)]
      (doall (map #(:id %) rs)))))

(defn insert-fact
  ""
  [metric time-id db-connection]
  (db/with-connection db-connection
    (db/with-query-results rs [(build-insert-fact-statement metric time-id)]
      (doall (map #(:id %) rs)))))

(defn process-minute
  ""
  [time-stamp metrics db-connection]
  (let [time-id (first (insert-time time-stamp db-connection))]
    (doseq 
      [metric metrics]
      (insert-fact metric time-id db-connection))))

(defn parse-time-stamp
  ""
  [data]
  (let [parts (s/split data #" ")
        date (s/split (first parts) #"-")
        time (s/split (second parts) #":")]
    {:year (parse-int (first date))
     :month (parse-int (second date))
     :day (parse-int (nth date 2))
     :hour (parse-int (first time))
     :minute (parse-int (second time))}))

(defn parse-metric
  ""
  [metric]
  (let [metric-part (s/split metric #"=")
        metric-name (first metric-part)
        metric-data (second metric-part)
        metric-name-part (s/split metric-name #"\|")
        metric-data-part (s/split metric-data #",")]
    {:entity (first metric-name-part)
     :activity (second metric-name-part)
     :throughput (parse-int (first metric-data-part))
     :mode (parse-int (second metric-data-part))
     :vigintile (parse-int (nth metric-data-part 2))}))

(defn parse-metrics 
  ""
  [metrics]
  (map #(parse-metric %) (s/split metrics #":")))

(defn process-data-line
  ""
  [data-line db-connection]
  (let [data-line-part (s/split data-line #"\t")
        time-stamp (parse-time-stamp (first data-line-part))
        metrics (into [] (parse-metrics (second data-line-part)))]
    (process-minute time-stamp metrics db-connection)))

(defn process-data-file
  ""
  [data-file db-connection]
  (with-open [rdr (reader data-file)]
    (doseq [data-line (line-seq rdr)]
      (process-data-line data-line db-connection))))

(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/feedmetrics"
               :user "root"
               :password "*****"})
