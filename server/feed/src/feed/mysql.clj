(ns feed.mysql
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))

(require '[feed.settings :as prop])

(def db-spec {:classname "com.mysql.jdbc.Driver"
              :subprotocol "mysql"
              :subname (str "//" prop/sql-host ":3306/" prop/sql-db-name)
              :user prop/sql-db-user
              :password prop/sql-db-password})

(defn pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec)) 
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))] 
    {:datasource cpds}))

(def pooled-db (delay (pool db-spec)))
(defn connection [] @pooled-db)

(def load-participant-from-db-command "{ call FetchParticipant(?) }")
(def load-friends-from-db-command "{ call FetchFriends(?) }")
(def save-participant-to-db-command "{ call UpsertParticipant(?) }")
(def save-friend-to-db-command "{ call UpsertFriends(?, ?) }")
