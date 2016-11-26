# manually setting up the load test on AWS

Here are the scripts that I use to conduct this research.

## one time only setup

Before running your first tests, you will need to download some open source software and copy the files to the correct folders here. 

file | folder
--- | ---
apache-cassandra-2.2.6-bin.tar.gz | cassandra
elasticsearch-2.3.3.tar.gz | elasticsearch
kibana-4.5.1-linux-x64.tar.gz | elasticsearch
kong-0.8.3.aws.rpm | kong
redis-3.2.1.tar.gz | redis
solr-5.3.1.tar.gz | solr
lein | feed
apache-maven-3.3.9-bin.tar.gz | feed 
jdk-7u79-linux-x64.tar.gz | feed
kafka_2.11-0.9.0.1.tar.gz | kafka

## installing components that are part of this repo

You will also need to build the components here and copy them to the proper folders.

```bash
cd clojure-news-feed/server/support
mvn clean install
cd ../feed2
sbt
compile
assembly
exit
cp target/scala-2.11/news-feed-assembly-0.1.0-SNAPSHOT.jar ../aws/feed2
cd ../feed3
mvn package
cp target/newsfeed-dropwizard-1.0.0-SNAPSHOT.jar ../aws/feed3
cd ../../client/perf3
sbt
compile
assembly
exit
cp target/scala-2.11/news-feed-performance-assembly-1.0.jar ../../server/aws/elasticsearch
cd ../perf4
mvn package 
cp target/kong-logger-service-1.0.0-SNAPSHOT-fat.jar ../../server/aws/elasticsearch
cd ../../server/feed
cp etc/run.sh ../aws/feed
cp etc/Dockerfile ../aws/feed
```

## starting a test run

First, go to your AWS dashboard and allocate a dev MySql instance in RDS. I use a db.m4.large with 100 GB SSD. Next, go to EC2 and allocate 7 instances. I use m4.large with 10GB ESB HD. For testing, I keep all the relevant ports open to the Internet because it is easier to diagnose problems that way. See ports.txt for the list of ports.

Copy build/hosts.py.empty to build/hosts.py then edit that python script specifying the IP addresses for each host. You will notice that this file needs the internal IP address for both Cassandra and Redis. You can obtain those IP addresses when you ssh to those machines and run the hostname -I command.

You will need to edit the python scripts in the build folder in order to provide your AWS credentials correctly.

After the hosts.py is ready, run the following.

```bash
cd build
./build.sh
cd ..
# edit copy.sh to comment / uncomment out the lines which install news feed service you are testing with
./copy.sh /path/to/your/aws.pem
```

Now you can ssh to each machine. All you have to do with most of these is simply run the install.sh script.

1. ssh to cassandra and run install.sh
2. ssh to kafka and run install.sh
3. ssh to redis and run install.sh
4. ssh to elasticsearch and run install-with-es.sh
5. ssh to feed2 or feed3 and run install.sh (see below for feed)
6. ssh to kong and run install.sh
7. ssh back to elasticsearch and run run.sh
8. ssh to load and run install.sh then either runload2.sh (if testing feed or feed2) or runload3.sh (if testing feed3)

After you install cassandra and before you install the news feed, be sure to create the casandra schema.

```bash
cd path/to/clojure-news-feed/server/feed/etc
cqlsh {cassandra} <schema.cassandra.sql
```

## Running the Clojure feed using docker

Starting the Clojure service is more complicated. The easiest way is to use Docker.

```bash
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user
exit
# ssh back in
docker build -t feed .
docker run -d --net=host feed
```
