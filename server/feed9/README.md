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

We see a little under 8,000 create outbound RPM with 5 ms average latency for about 10 minutes. After that, the CPU maxes out and throughput grinds to a halt. Not sure why. Nothing in the application log indicates any problems.







