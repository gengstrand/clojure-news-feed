(ns feed.util)

(defn parse-int [s]
  (Integer. (re-find  #"\d+" s )))

(defn extract-id [s]
  (Integer. (second (re-find #"/participant/(\d+)" s))))

(defn embed-id [id]
  (str "/participant/" id))
