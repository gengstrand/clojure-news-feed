(ns feed.daos.inbound
  (:require [feed.daos.widecolumn :as wc])
  (:import (com.datastax.oss.driver.api.core CqlIdentifier)))

(def formatter (org.joda.time.format.DateTimeFormat/forPattern "yyyy-MM-dd"))

(defn convert-inbound
  "convert a row from the result to a map"
  [row id]
  (let [occurred-instant (.getInstant row (CqlIdentifier/fromCql "Occurred"))
        occurred-seconds (.getEpochSecond occurred-instant)
        occurred-millis (.toMillis (java.time.Duration/ofSeconds occurred-seconds))]
        {:from (.getInt row (CqlIdentifier/fromCql "FromParticipantID"))
         :to id
         :occurred (.print formatter occurred-millis)
         :subject (.getString row (CqlIdentifier/fromCql "Subject"))
         :story (.getString row (CqlIdentifier/fromCql "Story"))}))

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
