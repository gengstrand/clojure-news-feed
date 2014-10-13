# perf

This perf project is a map reduce job, written in Cascalog, designed to take the output from kafka 
in the Clojure news feed service and prepare it for the ETL job that loads an 
OLAP cube to analyze this performance data.

This is functionally equivalent to the NewsFeedPerformance Java project.

## Usage

cd /path/to/perf/project

lein uberjar

cd /path/to/hadoop/home

rm -Rf /path/to/output/folder

bin/hadoop jar /path/to/perf/project/target/perf-0.1.0-SNAPSHOT-standalone.jar /path/to/kafka/output/folder/path/to/output/folder >/dev/null


## License

Copyright © 2014 Glenn Engstrand

Distributed under the Eclipse Public License, the same as Clojure.
