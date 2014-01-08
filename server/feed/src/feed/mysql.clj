(ns feed.mysql)

(def connection {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/feed"
               :user "feed"
               :password "feed"})

(def load-participant-from-db-command "{ call FetchParticipant(?) }")
(def load-friends-from-db-command "{ call FetchFriends(?) }")
(def save-participant-to-db-command "{ call UpsertParticipant(?) }")
(def save-friend-to-db-command "{ call UpsertFriends(?, ?) }")
