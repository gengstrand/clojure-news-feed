# etl

A command line tool that inputs the summarized output from the 
clojure-news-feed service as reported by the kafka feed topic
and loads a mysql database for the purposes of mondrian OLAP reporting

## Usage

In order to capture the performance metrics, you will need to collect the kafka topic messages into a file while the load test is running.

bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic feed --from-beginning >perf.log

After the load test completes, you will  need to copy that perf.log to where you can run the news feed performance map reduce job. For my 2 hours long load test, I could safely run Hadoop in single node mode.

cd ~/oss/hadoop/hadoop-1.0.4

rm -Rf outputFeedData

bin/hadoop jar news-feed-performance-0.0.1-SNAPSHOT.jar com.dynamicalsoftware.feed.mapreduce.AggregatePerformanceData inputFeedData outputFeedData

cd ~/git/clojure-news-feed/client/etl

edit the mysql-db properties in src/etl/core.clj

lein repl

(process-data-file "~/oss/hadoop/hadoop-1.0.4/outputFeedData/part-00000" mysql-db)

## License

Copyright © 2014 Glenn Engstrand

Distributed under the Eclipse Public License, the same as Clojure.
