(ns load.mysql
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))

(require '[clojure.java.jdbc :as jdbc])

(def load-participant-from-db-command "{ call FetchParticipant(?) }")
(def load-friends-from-db-command "{ call FetchFriends(?) }")
(def mysql-host (System/getenv "MYSQL_HOST"))
(def mysql-port (System/getenv "MYSQL_PORT"))
(def db-spec 
  (if (nil? mysql-host)
    nil
    {:classname "com.mysql.jdbc.Driver"
               :subprotocol "mysql"
               :subname 
               (str "//" 
                    mysql-host 
                      ":"
                      (if (nil? mysql-port) "3306" mysql-port)
                      "/feed?useSSL=false")
               :user (System/getenv "MYSQL_USER")
               :password (System/getenv "MYSQL_PASSWORD")}))

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

(defn load-participant-from-db 
  "fetch this participant from the db"
  [id]
    (first (doall (jdbc/query (connection) [load-participant-from-db-command id]
                    :row-fn #(str (:moniker %))))))

