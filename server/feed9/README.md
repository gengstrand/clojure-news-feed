# Go API Server for newsfeed

## Overview

This folder contains a golang implementation of the news feed microservice. I started by using the
[go-server template](https://github.com/swagger-api/swagger-codegen/tree/master/modules/swagger-codegen/src/main/resources/go-server) from the [swagger-codegen](https://github.com/swagger-api/swagger-codegen) project.

## Dependencies

I used the following projects.

The go-server templates use [mux](https://github.com/gorilla/mux) as the request dispatcher.

Access to the feed MySql DB was via the standard golang [SQL API](https://golang.org/pkg/database/sql/) to this [MySql driver](https://github.com/go-sql-driver/mysql/).

The Redis cache was used to front access to the MySql DB with this [Redis client library](https://github.com/go-redis/redis).

I used the standard [Cassandra client library](https://github.com/gocql/gocql) for inbound and outbound entities.

I used this non-standard [Elasticsearch client library](https://olivere.github.io/elastic/) because it still supports version 2.

## Usage

You will need to install the dependencies.

```
 go get -u github.com/gorilla/mux
 go get -u github.com/go-sql-driver/mysql
 go get -u github.com/go-redis/redis
 go get -u github.com/gocql/gocql
 go get -u github.com/google/uuid
 go get -u gopkg.in/olivere/elastic.v3
```

Here is how to run the sample unit test

```
go test test/api_outbound_test.go
```

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

I tested this microservice using the standard load test environment for two hours (see the client/load folder in this repo). The average per minute throughput of output posts was 12,937 with an average duration of 8.7 ms, a median of 5 ms, and a 99th percentile of 23 ms. That means the golang version of the news feed service performed worse than the java versions, was on par with the node.js version and performed better than the python, scala, and clojure versions.
