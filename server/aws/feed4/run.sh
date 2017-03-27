cd clojure-news-feed/server/feed4
export MYSQL_USER="feed"
export MYSQL_PASS="feed1234"
export MYSQL_DB="feed"
export SEARCH_PATH="/feed/stories"
awk '/=/{printf "export %s\n", $1}' <~/env.list >env.list
. ./env.list
npm start
