(ns perf5.core-test
  (:require [clojure.test :refer :all]
            [perf5.core :refer :all]
            [clojure.data.json :as json]))

(def searcher-stats-numdocs 23)
(def searcher-stats-numdocs-json
  (str 
    "{\"searcher\":{\"stats\":{\"numDocs\":"
    searcher-stats-numdocs
    "}}}"))
  
(deftest parse-int-test
  (testing "FIXME, I fail."
    (is (= 5 (parse-int "5")))))

(deftest extract-num-docs-test
  (extract-num-docs (json/read-str searcher-stats-numdocs-json))
  (testing "extract numDocs from json"
     (is 
       (=
         searcher-stats-numdocs
         total-docs))))

(deftest refresh-stats-test 
  (refresh-stats 5 6 7 8)
  (let [s (refresh-stats 6 7 8 9)]
    (testing "keep single interval stats from running total stats"
      (is 
        (and (= (:index-total s) 1)
             (= (:index-time s) 1)
             (= (:query-total s) 1)
             (= (:query-time s) 1))))))

(def extract-result 1)

(defn set-extract-result 
  [value]
  (def extract-result value))

(deftest following-test 
  (following ["miss" 0 "hit" 2] "hit" set-extract-result)
  (testing "extract result following tag"
     (is (= extract-result 2))))
