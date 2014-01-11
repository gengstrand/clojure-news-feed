(ns feed.cassandra)

(require '[qbits.alia :as alia])
(require '[feed.settings :as prop])

(def cluster (alia/cluster prop/nosql-host))
(def session (alia/connect cluster "activity"))

(def load-inbound-from-db-command "select dateOf(occurred), fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc")
(def save-inbound-to-db-command "insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?)")
(def load-outbound-from-db-command "select dateOf(occurred), subject, story from Outbound where participantid = ? order by occurred desc")
(def save-outbound-to-db-command "insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)")

