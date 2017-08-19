cd /home/$USER/clojure-news-feed/server/feed5
docker build -t feed5:1.0 .
docker run -d --net=host feed5:1.0
