## sample news feed written in scala

Right now, this is an incomplete work in progress but eventually this will be 
identical in functionality to https://github.com/gengstrand/clojure-news-feed/tree/master/server/feed but written in scala instead of clojure.

This is a micro-service that uses spray so see https://github.com/spray/spray-template as an introduction to that.

## how I am running this on my laptop during initial development

cd /home/glenn/oss/kafka/kafka_2.11-0.8.2.1
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
bin/kafka-console-consumer.sh --zookeeper 127.0.0.1:2181 --topic feed --from-beginning

cd /home/glenn/oss/redis/redis-2.8.2/src
./redis-server
cd /home/glenn/Apps/apache-cassandra-1.2.2/bin
./cassandra -f

cd /home/glenn/git/clojure-news-feed/server/feed2
sbt
compile
assembly
exit
java -jar target/scala-2.11/feed2-assembly-0.1.jar src/main/resources/settings.properties



