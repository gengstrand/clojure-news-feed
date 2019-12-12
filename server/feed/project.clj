(defproject feed "0.1.0-SNAPSHOT"
  :description "news feed micro service in clojure"
  :url "http://www.dynamicalsoftware.com/software/architecture/oss/clojure"
  :repositories {"local" ~(str (.toURI (java.io.File. "/home/glenn/.m2/repository")))}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.8.0"]
                 [ring/ring-jetty-adapter "1.8.0"]
                 [org.eclipse.jetty/jetty-util "9.4.24.v20191120"]
                 [org.eclipse.jetty/jetty-io "9.4.24.v20191120"]
                 [org.eclipse.jetty/jetty-http "9.4.24.v20191120"]
                 [org.eclipse.jetty/jetty-continuation "9.4.24.v20191120"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.eclipse.jetty/jetty-server "9.4.24.v20191120"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [mysql/mysql-connector-java "5.1.38"]
                 [postgresql "9.1-901.jdbc4"]
                 [com.mchange/c3p0 "0.9.5.1"]
                 [com.taoensso/carmine "2.12.0"]
                 [net.spy/spymemcached "2.12.1"]
                 [clj-kafka "0.3.2"]
                 [cc.qbits/alia "2.2.3"]
                 [org.clojure/data.json "0.2.4"]
                 [clj-http "0.7.8"]
                 [log4j/log4j "1.2.17"]
                 [commons-logging/commons-logging "1.1"]
                 [commons-fileupload/commons-fileupload "1.3"]
                 [commons-codec/commons-codec "1.10"]
                 [com.dynamicalsoftware/feed.support.services "0.0.1-SNAPSHOT"]
                 [compojure "1.6.0"]]
  :plugins [[lein-ring "0.12.4"]
  	    [lein2-eclipse "2.0.0"]]
  :ring {:handler feed.handler/app}
  :main feed.handler
  :profiles {:uberjar {:aot :all}}
  :jvm-opts ["-server"
             "-Xms32M"
             "-Xmx256M"
             "-XX:NewRatio=5"
             "-XX:+UseConcMarkSweepGC"
             "-XX:+UseParNewGC"
             "-XX:MaxPermSize=64m"])
