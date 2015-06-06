(ns feed.search)

(require '[feed.settings :as prop])

(def outbound-core 
  (if 
    (nil? prop/service-config)
    nil
    (if 
      (.startsWith (:search-host prop/service-config) "http")
      (com.dynamicalsoftware.support.Search/server (:search-host prop/service-config))
      (com.dynamicalsoftware.support.Search/server (:search-host prop/service-config) "outbound"))))

(defn index
  "add this story to the search index"
  [from story]
  (com.dynamicalsoftware.support.Search/add outbound-core from story))

(defn search
  "search for these terms and return the matching list of senders"
  [terms]
  (set (into [] (com.dynamicalsoftware.support.Search/results outbound-core terms 100))))

  