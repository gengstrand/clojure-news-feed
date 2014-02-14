(ns feed.postgre)

(def load-participant-from-db-command "select moniker from FetchParticipant(?)")
(def load-friends-from-db-command "select FriendsID, ParticipantID from FetchFriends(?)")
(def save-participant-to-db-command "select id from UpsertParticipant(?)")
(def save-friend-to-db-command "select id from UpsertFriends(?, ?)")
