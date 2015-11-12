(ns feed.rdbms
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))

(require '[feed.settings :as prop])
(require '[feed.postgre :as p])
(require '[feed.mysql :as m])

(defn is-postgres 
  "determines whether or not we are using postgresql as the RDBMS"
  []
  (if (nil? prop/service-config) false 
      (.equals (:sql-vendor prop/service-config) "postgresql")))

(def db-spec 
  (if (nil? prop/service-config)
    nil
    {:classname 
              (if 
                (is-postgres)
                "org.postgresql.Driver"
                "com.mysql.jdbc.Driver")
               :subprotocol (:sql-vendor prop/service-config)
               :subname 
               (str "//" 
                    (:sql-host prop/service-config) 
                    (if 
                      (is-postgres)
                      ":5432/" 
                      ":3306/")
                    (:sql-db-name prop/service-config))
               :user (:sql-db-user prop/service-config)
               :password (:sql-db-password prop/service-config)}))

(defn pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec)) 
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               (.setMaxPoolSize 50)
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))] 
    {:datasource cpds}))

(def pooled-db (if (nil? db-spec) nil (delay (pool db-spec))))
(defn connection [] @pooled-db)

(def load-participant-from-db-command
  (if 
    (is-postgres)
    p/load-participant-from-db-command
    m/load-participant-from-db-command))
(def load-friends-from-db-command
  (if 
    (is-postgres)
    p/load-friends-from-db-command
    m/load-friends-from-db-command))
(def save-participant-to-db-command
  (if 
    (is-postgres)
    p/save-participant-to-db-command
    m/save-participant-to-db-command))
(def save-friend-to-db-command 
  (if 
    (is-postgres)
    p/save-friend-to-db-command
    m/save-friend-to-db-command))
