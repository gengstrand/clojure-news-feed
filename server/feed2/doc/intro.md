## how I am running this on my laptop during initial development

```bash
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
java -Djava.util.logging.config.file=src/main/resources/logging.properties -jar target/scala-2.11/feed2-assembly-0.1.jar src/main/resources/settings.properties
```

### some sample test curl calls

```bash
curl http://127.0.0.1:8080/participant/3
curl http://127.0.0.1:8080/friends/19
curl http://127.0.0.1:8080/inbound/3
curl -d 'terms=27309' http://127.0.0.1:8080/outbound/search
curl -d name=Griff http://localhost:8080/participant/new
curl -d from=368 -d to=371 http://localhost:8080/friends/new
curl -d from=19 -d occurred=2015-04-18 -d subject=test -d story=test http://localhost:8080/outbound/new
```

### how to truncate solr

```bash
curl -g 'http://localhost:8983/solr/outbound/update?stream.body=<delete><query>*:*</query></delete>&commit=true'
```

### how to clear redis

```bash
cd /home/glenn/oss/redis/redis-2.8.2/src
./redis-client
flushall
```

### how to truncate cassandra

```bash
cd /home/glenn/Apps/apache-cassandra-1.2.2/bin
./cqlsh
use activity;
truncate Inbound;
truncate Outbound;
```

