(ns feed.postgre
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))

(require '[feed.settings :as prop])

(def db-spec {:classname "org.postgresql.Driver"
               :subprotocol "postgresql"
               :subname (str "//" prop/sql-host ":5432/" prop/sql-db-name)
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

(def load-participant-from-db-command "select moniker from FetchParticipant(?)")
(def load-friends-from-db-command "select FriendsID, ParticipantID from FetchFriends(?)")
(def save-participant-to-db-command "select id from UpsertParticipant(?)")
(def save-friend-to-db-command "select id from UpsertFriends(?, ?)")
