(ns perf.core
  (:gen-class)
  (:require [clojure.string :as s]
            [cascalog.api :as c]
            [cascalog.logic.ops :as o]
            [cascalog.more-taps :refer [hfs-delimited]]))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn parse-data-line
  ""
  [line]
  (s/split line #"\|"))

(defn metrics [dir]
  (let [source (c/hfs-textline dir)]
    (c/<- [?year ?month ?day ?hour ?minute ?entity ?action ?count] 
          (source ?line) 
          (parse-data-line ?line :> ?year ?month ?day ?hour ?minute ?entity ?action ?count)
          (:distinct false))))

(defn roll-up-by-minute 
  ""
  [input-directory output-directory]
  (let [data-point (metrics input-directory)
        output (hfs-delimited output-directory :sinkmode :replace :delimiter ",")]
       (c/?<- output 
              [?year ?month ?day ?hour ?minute ?entity ?action ?total] 
              (data-point ?year ?month ?day ?hour ?minute ?entity ?action ?count) 
              (parse-int ?count :> ?cnt) 
              (o/sum ?cnt :> ?total))))

(defn -main
  ""
  [& args]
  (if 
    (= (count args) 2)
    (roll-up-by-minute (first args) (second args))
    (println "usage: hadoop jar perf-0.1.0-SNAPSHOT-standalone.jar input-folder output-folder")))
