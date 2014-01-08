(ns feed.cassandra)

(require '[qbits.alia :as alia])

; https://github.com/mpenet/alia

(def cluster (alia/cluster "localhost"))
(def session (alia/connect cluster "activity"))

(def load-inbound-from-db-command "select dateOf(occurred), fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc")
(def save-inbound-to-db-command "insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?)")
(def load-outbound-from-db-command "select dateOf(occurred), subject, story from Outbound where participantid = ? order by occurred desc")
(def save-outbound-to-db-command "insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)")

