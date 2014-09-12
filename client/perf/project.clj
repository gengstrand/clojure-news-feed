(defproject perf "0.1.0-SNAPSHOT"
  :description "news feed performance map reduce job in cascalog"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cascalog "2.0.0"]
                 [org.apache.hadoop/hadoop-core "1.2.1"]
                 [cascalog/cascalog-more-taps "2.1.1"]]
  :main ^:skip-aot perf.core
  :target-path "target/%s"
  :jvm-opts ["-Xms768m" "-Xmx768m"]
  :profiles {:uberjar {:aot :all}
  	    :dev {:dependencies [[org.apache.hadoop/hadoop-core "1.0.4"]]}})
