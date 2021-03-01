(ns feed.daos.relational
  (:import (org.jdbi.v3.core Jdbi)))

(def jdbi (atom "jdbc:h2:mem:feed;DB_CLOSE_DELAY=-1"))

(defn connect
  "initialize connection to relational database"
  []
  (let [host (or (System/getenv "MYSQL_HOST") "mysql")
        db (or (System/getenv "MYSQL_DB") "feed")
        user (or (System/getenv "MYSQL_USR") "feed")
        password (or (System/getenv "MYSQL_PWD") "feed1234")]
        (swap! jdbi (fn [cs] (Jdbi/create (str "jdbc:mysql://" host ":3306/" db) user password)))))

