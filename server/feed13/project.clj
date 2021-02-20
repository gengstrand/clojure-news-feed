(defproject feed "0.1.0-SNAPSHOT"
  :description "news feed micro service in clojure"
  :url "http://www.dynamicalsoftware.com/software/architecture/oss/clojure"
  :repositories {"local" ~(str (.toURI (java.io.File. "/home/glenn/.m2/repository")))}
  :dependencies [[org.clojure/clojure "1.10.1"]
  		 [org.clojure/data.json "1.0.0"]
		 [ring/ring-json "0.5.0"]
  		 [compojure "1.6.1"]
 		 [ch.qos.logback/logback-classic "1.2.3"]
		 [javax.servlet/servlet-api "2.5"]
                 [com.appsflyer/donkey "0.4.2"]]
  :main feed.start
  :profiles {:uberjar {:aot :all}}
  :jvm-opts ["-Dvertx.threadChecks=false"
             "-Dvertx.disableContextTimings=true"])
