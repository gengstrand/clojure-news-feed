(defproject feed "0.1.0-SNAPSHOT"
  :description "news feed micro service in clojure on vert.x"
  :url "https://github.com/gengstrand/clojure-news-feed/blob/master/server/feed13/README.md"
  :repositories {"local" ~(str (.toURI (java.io.File. "/home/glenn/.m2/repository")))}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.jdbi/jdbi3-core "3.16.0"]
                 [redis.clients/jedis "3.5.1"]
                 [com.datastax.oss/java-driver-core "4.10.0"]
                 [org.elasticsearch.client/elasticsearch-rest-high-level-client "7.11.1"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [javax.servlet/servlet-api "2.5"]
                 [mysql/mysql-connector-java "8.0.23"]
                 [com.appsflyer/donkey "0.4.2"]]
  :main feed.start
  :profiles {:uberjar {:aot :all}
             :test {:dependencies [[mockery "0.1.4"]]}}
  :jvm-opts ["-Dvertx.threadChecks=false"
             "-Dvertx.disableContextTimings=true"])
