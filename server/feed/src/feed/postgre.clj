(ns feed.postgre)

(def connection {:classname "org.postgresql.Driver"
               :subprotocol "postgresql"
               :subname "//127.0.0.1:5432/feed"
               :user "feed"
               :password "feed"})

(def load-participant-from-db-command "select moniker from FetchParticipant(?)")
(def load-friends-from-db-command "select FriendsID, ParticipantID from FetchFriends(?)")
(def save-participant-to-db-command "select id from UpsertParticipant(?)")
(def save-friend-to-db-command "select id from UpsertFriends(?, ?)")
