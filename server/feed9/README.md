# Go API Server for newsfeed

## Overview

This folder contains a golang implementation of the news feed microservice. I started by using the
[go-server template](https://github.com/swagger-api/swagger-codegen/tree/master/modules/swagger-codegen/src/main/resources/go-server) from the [swagger-codegen](https://github.com/swagger-api/swagger-codegen) project.

## Dependencies

I used the following projects.

The go-server templates use [mux](https://github.com/gorilla/mux) as the request dispatcher.

Access to the feed MySql DB was via the standard golan [SQL API](https://golang.org/pkg/database/sql/) to this [MySql driver](https://github.com/go-sql-driver/mysql/).

The Redis cache was used to front access to the MySql DB with this [Redis client library](https://github.com/go-redis/redis).

I used the standard [Cassandra client library](https://github.com/gocql/gocql) for inbound and outbound entities.

I used this non-standard [Elasticsearch client library](https://olivere.github.io/elastic/) because it still supports version 2.

## Usage

Here is how to build the docker image.

```
docker build -t feed9:1.0 .
```

Here is how to deploy the docker image.

```
cd ../k8s
kubectl create -f feed9-deployment.yaml
```

## Load Testing

We tested this microservice using our standard load test environment. For the first 10 minutes, it maintained a steady outbound post throughput of 5,000 RPM with an average duration of 25 ms and a 99th percentile of 111 ms. After that, performance became very sporatic. Throughput ranged from 13 to 3,000 RPM and average latency ranged from 2 to 91 seconds. There were very few messages in the application log indicating loss of connectivity to cassandra, mysql, and elasticsearch but not enough to account for the destabilization. Nothing in the data store metrics indicated any loss of capability.












