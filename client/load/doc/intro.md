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

```bash
kubectl get pods | awk '/cassandra/{ printf "kubectl port-forward %s 9042:9042 &\n", $1 }' | sh
export CASSANDRA_HOST=127.0.0.1
export CASSANDRA_PORT=9042
kubectl get pods | awk '/mysql/{ printf "kubectl port-forward %s 3306:3306 &\n", $1 }' | sh
export MYSQL_HOST=127.0.0.1
export MYSQL_PORT=3306
export MYSQL_USER=feed
export MYSQL_PASSWORD=feed1234
kubectl get pods | awk '/redis/{ printf "kubectl port-forward %s 6379:6379 &\n", $1 }' | sh
export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
kubectl get pods | awk '/elasticsearch/{ printf "kubectl port-forward %s 9200:9200 &\n", $1 }' | sh
export ELASTIC_HOST=127.0.0.1
export ELASTIC_PORT=9200
kubectl get pods | awk '/feed/{ printf "kubectl port-forward %s 8080:8080 &\n", $1 }' | sh
export FEED_HOST=127.0.0.1
export FEED_PORT=8080
export USE_JSON=true
export USE_GRAPHQL=false
java -jar target/load-0.1.0-SNAPSHOT-standalone.jar --integration-test
```

