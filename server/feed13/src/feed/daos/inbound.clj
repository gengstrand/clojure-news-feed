(ns feed.daos.inbound
  (:require [feed.daos.widecolumn :as wc])
  (:import (com.datastax.oss.driver.api.core CqlIdentifier)
  	   (org.joda.time DateTime)))

(defn convert-inbound
  "convert a row from the result to a map"
  [row id]
  {:from (.getLong (CqlIdentifier/fromCql "FromParticipantID"))
   :to id
   :occurred (.toString (DateTime. (.getLocalDate (CqlIdentifier/fromCql "Occurred"))) "yyyy-MM-dd")
   :subject (.getString (CqlIdentifier/fromCql "Subject"))
   :story (.getString (CqlIdentifier/fromCql "Story"))})

(defn fetch
  "fetch the inbound news feed items for a participant"
  [id]
  (let [cql (str "select toTimestamp(occurred) as Occurred, FromParticipantID, Subject, Story from Inbound where participantid = " id " order by occurred desc")
        s (.execute @wc/cassandra cql)]
	(map #(convert-inbound % id) (.all s))))

(defn create
  "create an inbound news feed item for a participant"
  [from to occurred subject story]
  (let [cleansed-subject (.replaceAll subject "'" "")
        cleansed-story (.replaceAll story "'" "")
	cql (str "insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (" to ", " from ", now(), '" cleansed-subject "', '" cleansed-story "') using ttl 7776000")]
	(.execute @wc/cassandra cql))
  {:from from :to to :occurred occurred :subject subject :story story})
