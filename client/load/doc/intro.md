# Introduction to load

This is the load test application used to gain performance insight into the Clojure news feed service.

## usage

cd ~/git/clojure-news-feed/client/load

edit the host and port variables in src/load/handler.clj

If you wish, then you can edit any or all of the global variables near the top of src/load/core.clj

lein uberjar

java -jar target/load-0.1.0-SNAPSHOT-standalone.jar concurrent-users percent-searches

where concurrent-users controls the number of threads running the load test simultaneously

percent-searches controls what percentage of thse concurrent-user threads will be running the keyword search

For my load test run, I used 100 concurrent users running the search test 10 percent of the time

In order to capture the performance metrics, you will need to collect the kafka topic messages into a file.

bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic feed --from-beginning >perf.log



