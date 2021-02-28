(ns feed.daos.outbound
  (:require [feed.daos.widecolumn :as wc]
            [feed.daos.search :as s])
  (:import (com.datastax.oss.driver.api.core CqlIdentifier)))

(defn convert-outbound
  "convert a row from the result to a map"
  [row id]
  {:from id
   :occurred (.toString (.getInstant row (CqlIdentifier/fromCql "Occurred")))
   :subject (.getString row (CqlIdentifier/fromCql "Subject"))
   :story (.getString row (CqlIdentifier/fromCql "Story"))})

(defn fetch
  "fetch the outbound news feed items for a participant"
  [id]
  (let [cql (str "select toTimestamp(occurred) as Occurred, Subject, Story from Outbound where participantid = " id " order by occurred desc")
        s (.execute @wc/cassandra cql)]
        (map #(convert-outbound % id) (.all s))))

(defn create
  "create an outbound news feed item for a participant"
  [from occurred subject story]
  (let [cleansed-subject (.replaceAll subject "'" "")
        cleansed-story (.replaceAll story "'" "")
        cql (str "insert into Outbound (ParticipantID, Occurred, Subject, Story) values (" from ", now(), '" cleansed-subject "', '" cleansed-story "') using ttl 7776000")]
        (.execute @wc/cassandra cql))
  {:from from :occurred occurred :subject subject :story story})

(defn search
  "search participants who posted this content"
  [keywords]
  (s/search keywords))

(defn index
  "index participant story as searchable"
  [from story]
  (s/index (fn [doc]
               (.field doc "sender" from)
               (.field doc "story" story))))