(ns feed.mysql)

(def load-participant-from-db-command "{ call FetchParticipant(?) }")
(def load-friends-from-db-command "{ call FetchFriends(?) }")
(def save-participant-to-db-command "{ call UpsertParticipant(?) }")
(def save-friend-to-db-command "{ call UpsertFriends(?, ?) }")
