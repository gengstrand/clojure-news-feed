(defproject feed "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :repositories {"local" ~(str (.toURI (java.io.File. "/home/glenn/.m2/repository")))}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.3.0-alpha5"]
                 [mysql/mysql-connector-java "5.0.8"]
                 [postgresql "9.1-901.jdbc4"]
                 [com.mchange/c3p0 "0.9.2.1"]
                 [com.taoensso/carmine "2.4.0"]
                 [clj-kafka "0.1.2-0.8"]
                 [cc.qbits/alia "1.10.2"]
                 [log4j/log4j "1.2.17"]
                 [commons-logging/commons-logging "1.1"]
                 [commons-fileupload/commons-fileupload "1.3"]
                 [com.dynamicalsoftware/feed.support.services "0.0.1-SNAPSHOT"]
                 [compojure "1.1.6"]]
  :plugins [[lein-ring "0.8.8"]]
  :ring {:handler feed.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})