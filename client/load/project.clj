(defproject load "0.1.0-SNAPSHOT"
  :description "load test for news feed micro-service"
  :url "http://glennengstrand.info/software/architecture/oss/clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.4"]
                 [clj-http "0.7.8"]]
  :plugins [[lein2-eclipse "2.0.0"]]
  :main load.handler
  :aot [load.handler])
