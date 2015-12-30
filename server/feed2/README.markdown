## sample news feed written in scala

This is a rudimentary news feed micro-service, written in Scala, with the same features as https://github.com/gengstrand/clojure-news-feed/tree/master/server/feed which is written in Clojure.

This is a micro-service that used to be hosted in spray but I found that I could more than double the throughput when I moved it over to using finatra.

This micro-service uses the following technologies; kafka, redis, solr, cassandra, mysql, and postgresql.

See doc/intro.md for a more detailed explanation on how to get everything up and running. You can also just use the docker image https://hub.docker.com/r/gengstrand/scala-newsfeed/ instead. Be sure to add the following hosts when running the image; mysql_host, cassandra_host, redis_host, kafka_host, solr_host.

I did this so that I could be able to compare and contrast Scala and Clojure when it comes to writing heterogeneous data source micro-services. See the following blog on what I discovered.

http://glennengstrand.info/software/architecture/oss/scala

