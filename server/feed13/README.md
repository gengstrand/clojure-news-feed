# news feed server built with clojure on donkey

This project is the code behind a 2021 blog where I was [Revisiting Clojure](https://glennengstrand.info/software/architecture/microservice/clojure/vertx) by
building and testing the news feed service in Clojure on [Donkey](https://github.com/AppsFlyer/donkey) which is an HTTP server that is built on [Vert.x](https://vertx.io/)

## Internals

Dependencies that this project uses.

library | group | artifact | version | url
------- | ----- | -------- | ------- | ---
mysql | org.jdbi | jdbi3-core | 3.16.0 | [Jdbi 3 Developer Guide](https://jdbi.org)
mysql | mysql | mysql-connector-java | 8.0.23 | [MySql Connector/J](https://dev.mysql.com/doc/connector-j/8.0/en/)
redis | redis.clients | jedis | 3.5.1 | [repo](https://github.com/redis/jedis)
cassandra | com.datastax.oss | java-driver-core | 4.10.0 | 4.10.0 | [repo](https://github.com/datastax/java-driver)
cassandra | com.datastax.oss | java-driver-query-builder | 4.10.0 | [repo](https://github.com/datastax/java-driver)
cassandra | com.datastax.oss | java-driver-mapper-runtime | 4.10.0 | [repo](https://github.com/datastax/java-driver)
elasticsearch | org.elasticsearch.client | elasticsearch-rest-high-level-client | 7.11.1 | [Java High Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)
json | org.clojure | data.json | 1.0.0 | [repo](https://github.com/clojure/data.json)

## Usage

Some dev friendly commands to get going.

```bash
lein test
lein uberjar
docker build -t feed13:1.0 .
cd ../k8s
kubectl create -f feed13-deployment.yaml
```

## Performance Under Load

Create outbound is reactive (i.e. returns before completing) and both create participant and friends are synchronous.

entity | operation | throughput (RPM) | mean latency (ms) | 99th percentile latency (ms)
------ | --------- | ---------------- | ---------------------- | ---------------------------------
outbound | create | 15,098 | 3 | 12
participant | create | 2,567 | 17 | 31
friends | create | 1,732 | 18 | 29