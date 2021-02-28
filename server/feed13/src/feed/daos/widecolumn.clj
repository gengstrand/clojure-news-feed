(ns feed.daos.widecolumn
  (:import (com.datastax.oss.driver.api.core CqlSession CqlIdentifier)
           (java.net InetSocketAddress)))

(def cassandra (atom ""))

(defn connect
  "connect to cassandra"
  []
  (let [h (or (System/getenv "NOSQL_HOST") "cassandra")
        ks (or (System/getenv "NOSQL_KEYSPACE") "activity")
        s (-> (CqlSession/builder)
              (.addContactPoint (InetSocketAddress. h 9042))
              (.withLocalDatacenter "datacenter1")
              (.withKeyspace (CqlIdentifier/fromCql ks))
              (.build))]
        (swap! cassandra (fn [old] s))))



