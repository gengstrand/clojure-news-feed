# news feed microservices

Part of what I do as a Software Architect is to evaluate and recommend various technologies as they relate to microservice development. Just reading other blogs or vendor originated marketing collateral is not sufficient. 

When I decide to learn more about a particular technology or programming language, I use it to build a rudimentary news feed microservice. I document the developer experience. I subject it to various load tests whose performance is measured. Finally, I collect those measurements and analyze them. Since each news feed implementation has feature parity, I can blog about how the technology under evaluation compares to the other technologies previously implemented here.

All of the code written by me in these evaluations is open source and is available here in this repository. Here is how it is organized.

## server

These components are expected to be run on the server(s).

### feed

When I started this repo, a big trend had started in Java to use new programming languages, designed to run in the Java Virtual Machine, that support Functional Programming concepts. Clojure is a variant of Lisp that is one of the leaders in this trend. The question that I wanted to answer was this. Is Clojure ready for delivering services at web scale? This folder contains a basic news feed web service written in Clojure that I blogged about there.

https://glennengstrand.info/software/architecture/oss/clojure

### feed2

Scala originally gained recognition with the big data community through the Apache Spark project which is basically perceived as a faster form of map reduce. Its creator, Martin Odersky, advocated that Scala demonstrates that you can have object orientation on equal footing with functional programming. I covered the Scala implementation and how it was different from the Clojure version in terms of code.

https://glennengstrand.info/software/architecture/oss/scala

I blogged about the performance differences.

https://glennengstrand.info/software/performance/scala/clojure

I blogged about how this microservice performs when running with MySql, with PostGreSql, and with Docker.

https://glennengstrand.info/software/performance/mysql/postgres/docker

I documented my research into how this microservice performed when integrated with ElasticSearch and with Solr for keyword based search.

https://glennengstrand.info/software/performance/elasticsearch/solr

### feed3

I returned back to Java and blogged about how the DropWizard version compared to the Clojure version of the news feed.

https://glennengstrand.info/software/performance/clojure/dropwizard

### feed4

For something completely different, I blogged about a news feed microservice implementation in Node.js and how it compared with the DropWizard version.

https://glennengstrand.info/software/performance/nodejs/dropwizard

### feed5

This time, the news feed microservice is implemented in Python running on Flask.

https://glennengstrand.info/software/performance/nodejs/python

### feed6

This time, the scala microservice was rewritten in an attempt to lower complexity and improve performance.

https://glennengstrand.info/software/architecture/scala/rewrite

### feed7

News feed microservice built on top of Hyperledger Composer.

https://www.infoq.com/articles/evaluating-hyperledger-composer

### feed8

News feed microservice implemented in Java with Spring Boot

https://glennengstrand.info/software/performance/springboot/dropwizard

### feed9

News feed microservice implemented in golang.

https://glennengstrand.info/software/architecture/microservice/golang

### feed 10

News feed microservice implemented in TypeScript and GraphQL

https://glennengstrand.info/software/architecture/microservice/graphql

### feed 11

news feed microservice in Scala on Vert.x

### feed 12

news feed microservice in Scala on Play

### solr

The supporting directory structure and configuration files needed to augment an instance of Solr to support keyword search capability for the news feed on outbound activity.

### support

This Java project builds a library used by the feed service for Solr integration and for publishing custom JMX performance metrics.

### swagger

Swagger templates and assets used to generate the api and resource classes for all feed services version 3 or greater.

### aws

This folder contains assets for standing up the service, and its dependencies, on Amazon Web Services, Google Compute Engine, and Windows Azure.

### k8s

This folder contains assets for standing up the service, and its dependencies, in Kubernetes.

https://glennengstrand.info/software/performance/eks/gke

### proxy

An API gateway, written in golang, that proxies requests from the load test app to the news feed service under test and sends performance data to the perf4 service.

### prometheus

The proxy service has been enhanced to be able to make performance data available to prometheus.

### miscellaneous

There is a [jupyter notebook](https://github.com/gengstrand/clojure-news-feed/blob/master/server/fc.ipynb) which explores an attempt to quantify code complexity in the various implementations. It is quite inconclusive.

There is an Apache Druid [injection spec](https://github.com/gengstrand/clojure-news-feed/blob/master/server/feedDruidSpec.json) in which you can explore the performance data that I have collected from the various implementations. This example assumes that you have installed Druid locally.

```
cd path/to/local/druid
./bin/start-micro-quickstart
# wait a bit then open a new bash session
cd path/to/this/repo
curl -X 'POST' -H 'Content-Type:application/json' -d @server/feedDruidSpec.json http://localhost:8081/druid/indexer/v1/task
# wait a bit
cd path/to/local/druid
./bin/dsql
select cloud, feed, avg(rpm) as rpm, sum(sum_duration) / sum(rpm) as avg_duration, 
APPROX_QUANTILE_DS(quantile_duration, 0.50) as p50,
APPROX_QUANTILE_DS(quantile_duration, 0.95) as p95,
APPROX_QUANTILE_DS(quantile_duration, 0.99) as p99,
avg(max_duration) as max_duration
from feed
where entity = 'outbound' and operation = 'POST'
group by cloud, feed
order by feed, cloud;

┌───────┬──────┬───────┬──────────────┬──────┬──────┬──────┬──────────────┐
│ cloud │ feed │ rpm   │ avg_duration │ p50  │ p95  │ p99  │ max_duration │
├───────┼──────┼───────┼──────────────┼──────┼──────┼──────┼──────────────┤
│ EKS   │ 1    │  4822 │           26 │ 25.0 │ 37.0 │ 41.0 │          322 │
│ GKE   │ 1    │  6316 │           19 │ 17.0 │ 33.0 │ 57.0 │          266 │
│ GKE   │ 10   │  9702 │            5 │  4.0 │  8.0 │ 12.0 │          241 │
│ GKE   │ 11   │ 13844 │            5 │  5.0 │ 11.0 │ 16.0 │          261 │
│ GKE   │ 12   │ 19941 │            4 │  4.0 │  8.0 │ 10.0 │          190 │
│ EKS   │ 2    │  8030 │           13 │ 13.0 │ 18.0 │ 33.0 │          167 │
│ GKE   │ 2    │  6983 │           18 │ 13.0 │ 41.0 │ 48.0 │          292 │
│ EKS   │ 3    │ 14193 │            5 │  5.0 │  8.0 │ 10.0 │          257 │
│ GKE   │ 3    │ 18436 │            4 │  4.0 │  7.0 │ 10.0 │          213 │
│ EKS   │ 4    │ 18770 │            6 │  6.0 │  8.0 │ 12.0 │          700 │
│ GKE   │ 4    │ 13806 │            4 │  4.0 │  7.0 │ 12.0 │          152 │
│ EKS   │ 5    │  6065 │           13 │ 13.0 │ 20.0 │ 29.0 │          144 │
│ GKE   │ 5    │  6046 │           14 │ 14.0 │ 24.0 │ 30.0 │          176 │
│ EKS   │ 6    │  9643 │           10 │  9.0 │ 15.0 │ 18.0 │          205 │
│ GKE   │ 6    │  9580 │            9 │  9.0 │ 14.0 │ 19.0 │          231 │
│ GKE   │ 8    │  6482 │            3 │  3.0 │  5.0 │  9.0 │           98 │
│ GKE   │ 9    │ 14296 │            7 │  4.0 │ 20.0 │ 29.0 │          303 │
└───────┴──────┴───────┴──────────────┴──────┴──────┴──────┴──────────────┘
Retrieved 16 rows in 5.15s.
```

## client

These applications are expected to be run on the client(s).

### load

This Clojure application is what I used to load test the feed web service on public clouds.

### offline analysis

These applications run offline in order to aggregate raw kafka data into per minute summary data.

#### NewsFeedPerformance

This Java project builds a Hadoop map reduce job that inputs the Kafka feed topic performance data and outputs a per minute summary of various metrics used to load the OLAP cube.

#### perf

The same map reduce job as NewsFeedPerformance only this time written in Clojure for Cascalog.

#### etl

This Clojure project takes the output from the Hadoop news feed performance map reduce job and loads a MySql database ready for use by Mondrian's Pentaho OLAP server.

#### perf2

The same map reduce job as NewsFeedPerformance only this time written in Scala for Apache Spark.

### perf3

Instead of a map reduce job, the news feed performance data is consumed from kafka where it is aggregated once a minute and sent to elastic search.

### perf4

A microservice written in Java with the vert.x framework. It is intended to be called by kong. It aggregates performance data then sends it to elastic search in batches. It does not require kafka.

### perf5

A CLI tool that queries either elastic search or solr performance statistics and writes out per time period metrics to the console.

### mobile/feed

A cross mobile platform GUI for the news feed microservice that uses the Ionic Framework.

### ml

Scripts used to analyze microservice performance data with machine learning algo.

https://glennengstrand.info/software/architecture/msa/ml

https://glennengstrand.info/software/architecture/ml/oss

## License

Copyright © 2013 - 2020 Glenn Engstrand

Distributed under the Eclipse Public License

https://www.eclipse.org/legal/epl-v10.html
