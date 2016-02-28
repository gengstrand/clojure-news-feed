# perf3

This scala application consumes the stream of performance messages from the feed topic in kafka (produced by the feed service), collects those messages on a per entity and operation basis then aggregates them as per minute throughput, mean, median, and 95th percentile metrics then publishes them to elastic search in a kibana friendly way.

## usage

/usr/lib/jvm/java-8-openjdk-amd64/bin/java -Djava.util.logging.config.file=logging.properties -jar target/scala-2.11/news-feed-performance-assembly-1.0.jar kafka_IP_address elastic_search_IP_address

## License

Copyright © 2013 - 2016 Glenn Engstrand

Distributed under the Eclipse Public License, the same as Clojure.
