(ns feed.cassandra)

(require '[qbits.alia :as alia])
(require '[feed.settings :as prop])

(def cluster (alia/cluster (:nosql-host prop/service-config)))
(def session (alia/connect cluster "activity"))

(alia/set-consistency! (keyword (:nosql-consistency prop/service-config)))

(def load-inbound-from-db-command "select dateOf(occurred), fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc")
(def save-inbound-to-db-command (str "insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?) using ttl " (:nosql-ttl prop/service-config)))
(def load-outbound-from-db-command "select dateOf(occurred), subject, story from Outbound where participantid = ? order by occurred desc")
(def save-outbound-to-db-command "insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)")

