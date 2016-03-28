# clojure-news-feed

I wanted to find out how the latest crop of modern web service technologies scale. So I wrote a basic news feed micro-service in Clojure that uses a lot of modern open source supporting technology.

There is a big trend in Java right now to use new programming languages, designed to run in the Java Virtual Machine, that support Functional Programming concepts. Clojure is a variant of Lisp that is one of the leaders in this trend. The question that I wanted to answer was this. Is Clojure ready for deliverying services at web scale?

I blogged about the Clojure implementation.

http://glennengstrand.info/software/architecture/oss/clojure

I covered the Scala implementation and how it was different from the Clojure version in terms of code.

http://glennengstrand.info/software/architecture/oss/scala

I blogged about the performance differences.

http://glennengstrand.info/software/performance/scala/clojure

I blogged about how this micro-service performs when running with MySql, with PostGreSql, and with Docker.

http://glennengstrand.info/software/performance/mysql/postgres/docker

## server

These components are expected to be run on the server(s).

### feed

A basic news feed web service written in Clojure.

### feed2

The same micro-service as feed only this time written in Scala.

### solr

The supporting directory structure and configuration files needed to augment an instance of Solr to support keyword search capability for the news feed on outbound activity.

### support

This Java project builds a library used by the feed service for Solr integration and for publishing custom JMX performance metrics.

## client

These applications are expected to be run on the client(s).

### load

This Clojure application is what I used to load test the feed web service on AWS.

### NewsFeedPerformance

This Java project builds a Hadoop map reduce job that inputs the Kafka feed topic performance data and outputs a per minute summary of various metrics used to load the OLAP cube.

### perf

The same map reduce job as NewsFeedPerformance only this time written in Clojure for Cascalog.

### perf2

The same map reduce job as NewsFeedPerformance only this time written Scala for Apache Spark.

### perf3

Instead of a map reduce job, the news feed performance data is aggregated once a minute and sent to elastic search.

### etl

This Clojure project takes the output from the Hadoop news feed performance map reduce job and loads a MySql database ready for use by Mondrian's Pentaho OLAP server.

## License

Copyright © 2013 - 2016 Glenn Engstrand

Distributed under the Eclipse Public License, the same as Clojure.
