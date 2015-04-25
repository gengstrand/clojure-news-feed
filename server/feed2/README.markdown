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

cd ~/oss/solr/solr-4.5.1/solr/example
java -Dsolr.solr.home=multicore -jar start.jar

cd /home/glenn/git/clojure-news-feed/server/feed2
sbt
compile
assembly
exit
java -jar target/scala-2.11/feed2-assembly-0.1.jar src/main/resources/settings.properties

## some sample test curl calls

curl http://127.0.0.1:8080/participant/3

curl http://127.0.0.1:8080/friends/19

curl http://127.0.0.1:8080/inbound/3

curl -d 'terms=27309' http://127.0.0.1:8080/outbound/search

curl -d name=Griff http://localhost:8080/participant/new

curl -d from=702 -d to=703 http://localhost:8080/friends/new

curl -d from=19 -d occurred=2015-04-18 -d subject=test -d story=test http://localhost:8080/outbound/new

how to truncate solr

curl -g 'http://localhost:8983/solr/outbound/update?stream.body=<delete><query>*:*</query></delete>&commit=true'

how to clear redis

flushall

how to truncate cassandra

use activity;
truncate Inbound;
truncate Outbound;

got a problem while running the load test

java -jar target/load-0.1.0-SNAPSHOT-standalone.jar 3 30

this creates 50 participants, then I start seeing this in the log and nothing else completes correctly

Apr 25, 2015 12:33:32 PM info.glennengstrand.io.Sql$ prepare
WARNING: com.mchange.v2.c3p0.impl.NewProxyPreparedStatement@103ec0d [wrapping: com.mysql.jdbc.PreparedStatement@1b26604: CALL UpsertParticipant('user+263497') ]
[WARN] [04/25/2015 12:33:33.186] [on-spray-can-akka.actor.default-dispatcher-7] [akka://on-spray-can/user/IO-HTTP/listener-0/52] Configured registration timeout of 1 second expired, stopping
[WARN] [04/25/2015 12:33:33.216] [on-spray-can-akka.actor.default-dispatcher-11] [akka://on-spray-can/user/IO-HTTP/listener-0/53] Configured registration timeout of 1 second expired, stopping


