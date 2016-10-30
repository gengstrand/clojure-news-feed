(ns feed.elastic
  (:import java.util.UUID)
  (:import org.apache.http.message.BasicHttpEntityEnclosingRequest)
  (:import org.apache.http.impl.DefaultBHttpClientConnection)
  (:import (org.apache.http.entity StringEntity ContentType)))

(require '[feed.settings :as prop])

(def json-media-type "application/json")

(defn create-search-request
  "query elastic search for these keywords"
  [keywords]
  (let [retVal (BasicHttpEntityEnclosingRequest.
                 "GET"
                 (str 
                   (:search-host prop/service-config)
                   "/_search?q="
                   keywords))]
    (.addHeader retVal "Accept" json-media-type)
    retVal))

(defn create-index-request
  "add a document to the index"
  [id key]
  (let [retVal (BasicHttpEntityEnclosingRequest.
                 "PUT"
                 (str 
                   (:search-host prop/service-config)
                   "/"
                   id
                   "-"
                   key))]
    (.addHeader retVal "Accept" json-media-type)
    retVal))

(defn create-entity
  "create a document to be sent to elastic search"
  [id key story]
  (let [doc 
        (str
          "{\"id\":\""
          key
          "\",\"sender\":"
          id
          "\"story\":\""
          story
          "\"}")
        content-type 
        (ContentType/create json-media-type "UTF-8")]
    (StringEntity. doc content-type)))

(defn send-to-elastic-search
  "send the request to elastic search"
  [request]
  (let [client (DefaultBHttpClientConnection. 1000)]
    (.sendRequestEntity client request)))

(defn index
  "add this story to the search index"
  [from story]
  (let [key (.toString (UUID/randomUUID))
        req (create-index-request from key)
        se (create-entity from key story)]
    (.setEntity req se)
    (send-to-elastic-search req)))

(defn search
  "search for these terms and return the matching list of senders"
  [terms]
  (let [req (create-search-request terms)]
    (send-to-elastic-search req)
    (let [response (.getContent (.getEntity req))]
      (with-open [rdr (clojure.java.io/reader response)]
        (reduce conj [] (line-seq rdr))))))
        
