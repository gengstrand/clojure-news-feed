(defproject perf5 "0.1.0-SNAPSHOT"
  :description "monitor search appliance stats"
  :url "http://glennengstrand.info"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "2.3.0"]]
  :plugins [[lein2-eclipse "2.0.0"]]
  :main ^:skip-aot perf5.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
