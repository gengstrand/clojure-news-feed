(ns feed.daos.search
  (:import (org.elasticsearch.client RestHighLevelClient RestClient RequestOptions)
           (org.elasticsearch.action.index IndexRequest)
           (org.elasticsearch.common.xcontent XContentFactory)
           (org.elasticsearch.action.search SearchRequest)
           (org.elasticsearch.search.builder SearchSourceBuilder)
           (org.elasticsearch.index.query QueryBuilders)
           (org.elasticsearch.rest RestStatus)
           (org.apache.http HttpHost)
           (java.util UUID)))

(def elasticsearch (atom ""))

(defn connect
  "open a connection to elastic search"
  []
  (let [h (or (System/getenv "SEARCH_HOST") "elasticsearch")
        es (-> (HttpHost. h 9200)
               (RestClient/builder)
               (RestHighLevelClient.))]
        (swap! elasticsearch (fn [old] es))))

(defn index
  "insert a document to the index"
  [builder]
  (let [id (.toString (UUID/randomUUID))
        b (XContentFactory/jsonBuilder)]
       (.startObject b)
       (.field b "id" id)
       (builder b)
       (.endObject b)
       (let [r (.source (IndexRequest. "feed" "stories" id) b)]
             (.index @elasticsearch r RequestOptions/DEFAULT))))

(defn search
  "query elastic search for participants who post stories with these keywords"
  [keywords]
  (let [sr (.types (SearchRequest. "feed") "stories")
        ssb (SearchSourceBuilder.)]
        (.query ssb (QueryBuilders/termQuery "story" keywords))
        (.source sr ssb)
        (let [resp (.search @elasticsearch sr RequestOptions/DEFAULT)]
             (if (= (.status resp) RestStatus/OK)
                 (let [hits (.getHits resp)]
                      (if (= (.getTotalHits hits) 0)
                          []
                          (map #(.get (.getSourceAsMap %) "sender") (seq (.getHits hits)))))
                 []))))
