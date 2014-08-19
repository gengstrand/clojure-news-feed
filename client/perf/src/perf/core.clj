(ns perf.core
  (:gen-class)
  (:require [clojure.string :as s]
            [cascalog.api :as c]
            [cascalog.logic.ops :as o]
            [cascalog.more-taps :refer [hfs-delimited]]))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn extract-entity-action 
  "extracts the entity and the action from the tuple and returns that as a vector"
  [tuple]
  [(first tuple) (second tuple)])

(defn extract-entity-actions 
  "generates the set of entity action pairs present in this collection of tuples"
  [tuples]
  (into 
    #{}
    (for [tuple tuples]
      (extract-entity-action tuple))))

(defn entity-action-match 
  "tests to see if the entity and action from the tuple match the specified entity and action"
  [entity-action tuple]
  (let [test (extract-entity-action tuple)]
	  (and 
	    (= (first entity-action) (first test))
      (= (second entity-action) (second test)))))

(c/defbufferfn agg-perf-data 
  "aggregate performance data into a format compatible with the NewsFeedPerformance map reduce job"
  [tuples]
  ; if we don't do this println then the output report will have the wrong numbers
  (println tuples)
  (let [entity-actions (extract-entity-actions tuples)
        retVal
			    (for [entity-action entity-actions]
			      (let [entity-action-counts 
                  (sort 
	                  (doall 
	                    (map 
	                      (fn [x] (parse-int (nth x 2))) 
	                       (doall 
	                         (filter 
	                           (fn [x] (entity-action-match entity-action x)) 
	                           tuples)))))]
		           (str 
		             (first entity-action)
		             "|"
		             (second entity-action)
		             "="
		             (count entity-action-counts)
		             ","
		             (nth entity-action-counts (/ (count entity-action-counts) 2))
		             ","
		             (nth entity-action-counts 
                    (/ 
                      (* 
                        (count entity-action-counts)
                        95)
                      100)))))]
       [(s/join ":" retVal)]))

(defn format-time-stamp 
  "needed to make output compatible with etl program"
  [year month day hour minute]
  (str 
    year
    "-"
    month
    "-"
    day 
    " "
    hour 
    ":"
    minute))

(defn parse-data-line
  "parses the kafka output into the corresponding fields"
  [line]
  (s/split line #"\|"))

(defn metrics [dir]
  (let [source (c/hfs-textline dir)]
    (c/<- [?year ?month ?day ?hour ?minute ?entity ?action ?count] 
          (source ?line) 
          (parse-data-line ?line :> ?year ?month ?day ?hour ?minute ?entity ?action ?count)
          (:distinct false))))

(defn roll-up-by-minute 
  "map reduce job that produces output usable by the etl program"
  [input-directory output-directory]
  (let [data-point (metrics input-directory)
        output (hfs-delimited output-directory)]
       (c/?<- output 
              [?ts ?rpt] 
              (data-point ?year ?month ?day ?hour ?minute ?entity ?action ?count) 
              (format-time-stamp ?year ?month ?day ?hour ?minute :> ?ts)
              (agg-perf-data ?entity ?action ?count :> ?rpt))))

(defn -main
  "main entry point for hadoop"
  [& args]
  (if 
    (= (count args) 2)
    (roll-up-by-minute (first args) (second args))
    (println "usage: hadoop jar perf-0.1.0-SNAPSHOT-standalone.jar input-folder output-folder")))
