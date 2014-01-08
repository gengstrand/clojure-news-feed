(ns feed.search)

(def outbound-core (com.dynamicalsoftware.support.Search/server "/home/glenn/oss/solr/solr-4.5.1/solr/example/multicore" "outbound"))

(defn index
  "add this story to the search index"
  [from story]
  (com.dynamicalsoftware.support.Search/add outbound-core from story))

(defn search
  "search for these terms and return the matching list of senders"
  [terms]
  (set (into [] (com.dynamicalsoftware.support.Search/results outbound-core terms 100))))

  