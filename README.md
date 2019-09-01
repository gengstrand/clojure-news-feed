# news feed microservices

Part of what I do as a Software Architect is to evaluate and recommend various technologies as they relate to microservice development. Just reading other blogs or vendor originated marketing collateral is not sufficient. 

When I decide to learn more about a particular technology or programming language, I use it to build a rudimentary news feed microservice. I document the developer experience. I subject it to various load tests whose performance is measured. Finally, I collect those measurements and analyze them. Since each news feed implementation has feature parity, I can blog about how the technology under evaluation compares to the other technologies previously implemented here.

All of the code written by me in these evaluations is open source and is available here in this repository. Here is how it is organized.

## server

These components are expected to be run on the server(s).

### feed

When I started this repo, a big trend had started in Java to use new programming languages, designed to run in the Java Virtual Machine, that support Functional Programming concepts. Clojure is a variant of Lisp that is one of the leaders in this trend. The question that I wanted to answer was this. Is Clojure ready for delivering services at web scale? This folder contains a basic news feed web service written in Clojure that I blogged about there.

http://glennengstrand.info/software/architecture/oss/clojure

### feed2

Scala originally gained recognition with the big data community through the Apache Spark project which is basically perceived as a faster form of map reduce. Its creator, Martin Odersky, advocated that Scala demonstrates that you can have object orientation on equal footing with functional programming. I covered the Scala implementation and how it was different from the Clojure version in terms of code.

http://glennengstrand.info/software/architecture/oss/scala

I blogged about the performance differences.

http://glennengstrand.info/software/performance/scala/clojure

I blogged about how this microservice performs when running with MySql, with PostGreSql, and with Docker.

http://glennengstrand.info/software/performance/mysql/postgres/docker

I documented my research into how this microservice performed when integrated with ElasticSearch and with Solr for keyword based search.

http://glennengstrand.info/software/performance/elasticsearch/solr

### feed3

I returned back to Java and blogged about how the DropWizard version compared to the Clojure version of the news feed.

http://glennengstrand.info/software/performance/clojure/dropwizard

### feed4

For something completely different, I blogged about a news feed microservice implementation in Node.js and how it compared with the DropWizard version.

http://glennengstrand.info/software/performance/nodejs/dropwizard

### feed5

This time, the news feed microservice is implemented in Python running on Flask.

http://glennengstrand.info/software/performance/nodejs/python

### feed6

This time, the scala microservice was rewritten in an attempt to lower complexity and improve performance.

http://glennengstrand.info/software/architecture/scala/rewrite

### feed7

News feed microservice built on top of Hyperledger Composer.

https://www.infoq.com/articles/evaluating-hyperledger-composer

### feed8

News feed microservice implemented in Java with Spring Boot

http://glennengstrand.info/software/performance/springboot/dropwizard

### feed9

News feed microservice implemented in golang.

http://glennengstrand.info/software/architecture/microservice/golang

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

http://glennengstrand.info/software/performance/eks/gke

## client

These applications are expected to be run on the client(s).

### load

This Clojure application is what I used to load test the feed web service on AWS.

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

http://glennengstrand.info/software/architecture/msa/ml

http://glennengstrand.info/software/architecture/ml/oss

## License

Copyright © 2013 - 2019 Glenn Engstrand

Distributed under the Eclipse Public License

https://www.eclipse.org/legal/epl-v10.html
