## how I am running this on my laptop during initial development

At the time of this writing, I am running on Vivid Vervet. Path names will be different depending on the versions of kafka, cassandra, redis, and solr that you installed.

```bash
cd ~/oss/kafka/kafka_2.11-0.9.0.1
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
bin/kafka-console-consumer.sh --zookeeper 127.0.0.1:2181 --topic feed --from-beginning

cd ~/oss/redis/redis-2.8.2/src
./redis-server
cd ~/Apps/apache-cassandra-1.2.2/bin
./cassandra -f

cd ~/oss/solr/solr-4.5.1/solr/example
java -Dsolr.solr.home=multicore -jar start.jar

cd ~/git/clojure-news-feed/server/feed2

sbt 'set test in assembly := {}' clean assembly

cat server/etcHosts4localhost >>/etc/hosts 

export APP_CONFIG=etc/settings.properties

java -DLOG_DIR=/tmp -DLOG_LEVEL=ALL -jar target/scala-2.11/news-feed-assembly-0.1.0-SNAPSHOT.jar
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
### how to truncate elastic search

```bash
curl -XDELETE http://localhost:9200/feed/stories/_query -d '{
   "query": {
      "match_all": {}
   }
}'
```

### how to clear redis

```bash
cd ~/oss/redis/redis-2.8.2/src
./redis-client
flushall
```

### how to truncate cassandra

```bash
cd ~/Apps/apache-cassandra-1.2.2/bin
./cqlsh
use activity;
truncate Inbound;
truncate Outbound;
```

### how to set up the postgres schema

```bash
sudo su -
service postgresql stop
vi /etc/postgresql/9.4/main/pg_hba.conf
# comment out the peer so that you can log in as feed via md5
# local   all             all                                     peer
host    all             all             127.0.0.1/32            md5
:w
:q
service postgresql start
su postgres
psql
create database feed;
create user feed with password 'feed1234';
grant all on database feed to feed;
\q
psql feed feed -W <schema.postgre.sql
```

## standing up the service in EC2

I used AWS for performance based research on how this service behaved under load. Here are some notes on that. Be advised that you would do things differently for a professional service where High Availability was important.

I set up all the EC2 instances the old fashioned way with the following ports open: 22, 9092, 2181, 8983, 9200, 9300, 5601, 9160, 9042, 8080, 6379, 7199, 9050, 9990. This is not secure but it was easy for me to test, monitor, and verify that all the services were working properly.

### running the service under docker

There is a Dockerfile for doing this in the etc folder. You can also use the docker image https://hub.docker.com/r/gengstrand/scala-newsfeed/ instead. Be sure to add the following hosts when running the image; mysql_host, cassandra_host, redis_host, kafka_host, solr_host. The port to export is 8080.

### setting up the dependent open source services

For the following scripts, assume that $PUBLIC_IP holds the IP address that you can reference from the VPC or Internet and $HOST_IP holds the IP address that the service binds to.

#### Relational Database

I used RDS where a database and credentials was set up.

##### Setting up MySql

mysql -h $PUBLIC_IP -u feed -p feed <~/git/clojure-news-feed/server/feed/etc/schema.mysql.sql

##### Setting up PostgreSql

psql -h $PUBLIC_IP -W feed feed <~/git/clojure-news-feed/server/feed/etc/schema.postgre.sql

#### Cassandra

```bash
vi cassandra.yaml

listen_interface: eth0
rpc_interface: eth0
seeds: $HOST_IP

bin/cassandra
```

I ran this from my local laptop where the news feed project source is located.

~/oss/src/apache-cassandra-2.1.11/bin/cqlsh $PUBLIC_IP <~/git/clojure-news-feed/server/feed/etc/schema.cassandra.sql

#### Redis

```bash
gunzip redis-3.0.5.tar.gz 
tar -xf redis-3.0.5.tar 
cd redis-3.0.5
sudo yum install gcc
sudo yum install tcl
cd deps
make hiredis jemalloc linenoise lua
cd ..
make
make test
redis.conf # daemonize yes
src/redis-server redis.conf
```

I tested the connection to redis from my local laptop 

~/oss/src/redis-3.0.5/src/redis-cli -h $PUBLIC_IP

#### Solr

* Upload, uncompress, and untar the solr binary archive then change directory to that folder.
* Make a compressed tar of the ~/git/clojure-news-feed/server/solr folder and upload it to the EC2 instance where Solr will be running.

```bash
bin/solr start
bin/solr create_core -c outbound
bin/solr stop
cd ..
gunzip solrconfig.tar.gz 
tar -xf solrconfig.tar 
cp -Rf solr/example/multicore/outbound/* solr-5.3.1/server/solr/outbound
cd solr-5.3.1
bin/solr start
```

In order to test this, I would point my web browser to http://$PUBLIC_IP:8983/solr

#### Kafka

Upload, uncompress, and untar the Kafka binary archive then change directory to that folder.

```bash
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
bin/kafka-server-start.sh -daemon config/server.properties
```

From my local laptop.

~/oss/src/kafka_2.11-0.9.0.1/bin/kafka-topics.sh --create --zookeeper $PUBLIC_IP:2181 --replication-factor 1 --partitions 1 --topic feed
