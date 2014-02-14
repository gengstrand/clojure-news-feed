# feed

An illustrative sample web service implementing a basic news feed capability. This was developed with the following dependencies...

clojure 1.5.1

iced tea 1.12.6

cassandra 1.2.2

postgresql 9.1

zookeeper 3.4.5

kafka 2.8.0

redis 2.8.2

solr 4.5.1

## Usage

This is a fun learning adventure for writing non-trivial web services in clojure so you have to start a lot of services in order to get it to work.

### starting all the services

cd ~/oss/apache-cassandra-1.2.2/bin

./cassandra -f

cd ~/oss/zk/zookeeper-3.4.5

bin/zkServer.sh start

cd ~/oss/kafka/kafka_2.8.0-0.8.0

bin/kafka-server-start.sh config/server.properties

cd ~/oss/redis/redis-2.8.2/src 

./redis-server

cd ~/oss/solr/solr-4.5.1/solr/example

java -Dsolr.solr.home=multicore -jar start.jar

cd ~/git/clojure-news-feed/server/feed

APP_CONFIG="/home/glenn/git/clojure-news-feed/server/feed/etc/config.clj"
export APP_CONFIG

lein ring uberjar

java -jar target/feed-0.1.0-SNAPSHOT-standalone.jar

### Initial, one time set up

cd ~/oss/kafka/kafka_2.8.0-0.8.0

bin/kafka-create-topic.sh --zookeeper localhost:2181 --replica 1 --partition 1 --topic feed

cd ~/git/clojure-news-feed/server/solr/example/multicore
cp solr.xml ~/oss/solr/solr-4.5.1/solr/example/multicore
mkdir ~/oss/solr/solr-4.5.1/solr/example/multicore/outbound
mkdir ~/oss/solr/solr-4.5.1/solr/example/multicore/outbound/conf
cp outbound/conf/* ~/oss/solr/solr-4.5.1/solr/example/multicore/outbound/conf

create a feed database with a user/password of feed/feed

psql feed <~/git/clojure-news-feed/server/feed/etc/schema.postgre.sql

cd ~/oss/apache-cassandra-1.2.2/bin

./cqlsh <~/git/clojure-news-feed/server/feed/etc/schema.cassandra.sql

You may need to edit ~/git/clojure-news-feed/server/feed/etc/config.cli

### Testing

curl -d name=Moe http://localhost:3000/participant/new

curl -d name=Larry http://localhost:3000/participant/new

curl -d name=Curly http://localhost:3000/participant/new

curl -d from=1 -d to=2 http://localhost:3000/friends/new

curl -d from=1 -d to=3 http://localhost:3000/friends/new

curl -d from=1 -d occurred="2014-01-03" -d subject="testing service" -d story="full end to end testing of the service" http://localhost:3000/outbound/new

curl http://localhost:3000/inbound/2

curl -d terms=testing http://localhost:3000/outbound/search

## License

Copyright © 2013 Glenn Engstrand

Distributed under the Eclipse Public License, the same as Clojure.
