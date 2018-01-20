(ns load.handler
  (:gen-class))

(require '[load.core :as service])

(def participant-batch-size 10)
(def min-friends 2)
(def max-friends 4)
(def subject-words 5)
(def story-words 150)
(def stories-per-user 10)
(def searches-per-user 5)
(def dictionary-size 40000)
(def participant-space 1000000)

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn create-participants 
  "create a batch of participants"
  [size]
  (into
    []
    (map 
      #(service/test-create-participant %)
      (take size (repeat (str "user " (rand-int participant-space)))))))

(defn log-timing
  "perform the function and return the metrics with the new timing information"
  [test-function]
  (try 
	  (let [before (System/currentTimeMillis)]
     (test-function)
	    {:latency (- (System/currentTimeMillis) before)
	     :errors 0})
	  (catch Exception e 
	    {:latency 0 :errors 1})))

(defn run-search 
  "fire off some searches"
  []
  (doseq 
    [pass (range searches-per-user)]
    (service/test-search (rand-int dictionary-size))))

(defn test-run-search
  "run the social broadcast and log the timing of it"
  []
  (while true 
    (println (log-timing run-search))))

(defn run-social-broadcast 
  "perform a social broadcast run"
  []
  (let [participants (create-participants participant-batch-size)
        inviters (into 
                   []
                   (map
                     #(rand-int %)
                     (take (/ participant-batch-size max-friends) (repeat participant-batch-size))))
        invited (into
                  []
                  (map
                    #(rand-int %)
                    (take (+ min-friends (rand-int (- max-friends min-friends))) (repeat participant-batch-size))))]
    (doseq
      [from (map #(nth participants %) inviters)]
      (doseq 
        [to (map #(nth participants %) invited)]
        (if 
          (not 
            (= from to))
          (service/test-create-friends from to))))
    (doseq
      [from (map #(nth participants %) invited)]
      (doseq
        [sender (take stories-per-user (repeat from))]
	      (service/test-create-outbound 
	        sender
	        (str "2014-01-0" (+ (rand-int 8) 1) "T19:25:51.490Z")
	        (reduce str (map #(str (rand-int %) " ") (take subject-words (repeat dictionary-size))))
	        (reduce str (map #(str (rand-int %) " ") (take story-words (repeat dictionary-size)))))))))

(defn test-run-social-broadcast
  "create some participants with social graph then make them active"
  []
  (while true 
    (println (log-timing run-social-broadcast))))

(defn initiate-concurrent-test-load 
  "spin up the threads for the concurrent load tests"
  [feed-host feed-port concurrent-users percent-searches use-json]
  (service/set-feed-host feed-host feed-port)
  (service/set-json-post use-json)
  (doseq [user (range concurrent-users)]
    (if 
      (<= (rand-int 100) percent-searches)
      (future (test-run-search))
      (future (test-run-social-broadcast)))))

(defn -main 
  "perform the load test"
  [& args]
  (let [feed-host (if (>= (count args) 1) (nth args 0) (System/getenv "FEED_HOST"))
        feed-port (if (>= (count args) 2) (nth args 1) (System/getenv "FEED_PORT"))
        concurrent-users (parse-int (if (>= (count args) 3) (nth args 2) (System/getenv "CONCURRENT_USERS")))
        percent-searches (parse-int (if (>= (count args) 4) (nth args 3) (System/getenv "PERCENT_SEARCHES")))
        use-json (if (> (count args) 4) true (= (System/getenv "USE_JSON") "true"))]
    (initiate-concurrent-test-load feed-host feed-port concurrent-users percent-searches use-json)))
