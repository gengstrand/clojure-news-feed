(defproject load "0.1.0-SNAPSHOT"
  :description "load test for news feed micro-service"
  :url "http://glennengstrand.info/software/architecture/oss/clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.4"]
                 [com.mchange/c3p0 "0.9.5.1"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [mysql/mysql-connector-java "5.1.38"]
                 [com.taoensso/carmine "2.12.0"]
                 [cc.qbits/alia "2.2.3"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [org.slf4j/slf4j-simple "1.6.4"]
                 [clj-http "0.7.8"]
                 [http-kit "2.3.0"]]
  :plugins [[lein2-eclipse "2.0.0"]]
  :repl-options { :timeout 120000 }
  :main load.handler
  :aot [load.handler])
