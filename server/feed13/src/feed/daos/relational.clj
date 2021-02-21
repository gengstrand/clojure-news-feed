(ns feed.daos.relational
  (:import (org.jdbi.v3.core Jdbi)))

(def jdbi (atom "jdbc:h2:mem:feed;DB_CLOSE_DELAY=-1"))

(defn create-db
  [handle]
  (.execute handle "create table Participant (ParticipantID int not null primary key auto_increment, Moniker varchar(50))" (into-array Object [])))

(defn connect
  "initialize connection to relational database"
  []
  (swap! jdbi (fn [cs] (Jdbi/create cs)))
  (let [h (.open @jdbi)]
    (try
      (create-db h)
      (catch Exception e (.println System/out (.getMessage e)))
      (finally (.close h)))))