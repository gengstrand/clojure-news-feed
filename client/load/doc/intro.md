# Introduction to test automation

This is the test automation application used to both assure functional correctness and to gain performance insight into each news feed microservice.

## usage

The load test was designed to work both as a Kubernetes job or just from your local laptop while developing.

### building for local run

```bash
cd ../load
lein uberjar
```

### building for docker run

```bash
docker build -t load:1.2 .
```

### running the load test locally

```bash
java -jar target/load-0.1.0-SNAPSHOT-standalone.jar host port concurrent-users percent-searches use-json use-graphql
```

Where host and port identifies how to connect to the feed service being tested.

Concurrent-users controls the number of threads running the load test simultaneously.

Percent-searches controls what percentage of thse concurrent-user threads will be running the keyword search.

If use-json is present, then request bodies will be in application/json format.

If use-graphql is present, then the graphql protocol will be used.

### running the load test as a Kubernetes job

```bash
cd ../k8s
kubectl create -f load_test.yaml
```

### running the integration test

The USE_JSON environment variable should be false for feeds 1 and 2. Otherwise, true.

The USE_GRAPHQL environment variable should be true for feed 10 only.

```bash
kubectl port-forward deployment/cassandra 9042:9042 &
export CASSANDRA_HOST=127.0.0.1
export CASSANDRA_PORT=9042
kubectl port-forward deployment/mysql 3306:3306 &
export MYSQL_HOST=127.0.0.1
export MYSQL_PORT=3306
export MYSQL_USER=feed
export MYSQL_PASSWORD=feed1234
kubectl port-forward deployment/redis 6379:6379 &
export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
kubectl port-forward deployment/elasticsearch 9200:9200 & 
export ELASTIC_HOST=127.0.0.1
export ELASTIC_PORT=9200
kubectl port-forward deployment/feed 8080:8080 &
export FEED_HOST=127.0.0.1
export FEED_PORT=8080
export USE_JSON=true
export USE_GRAPHQL=false
java -jar target/load-0.1.0-SNAPSHOT-standalone.jar --integration-test
```

