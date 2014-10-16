# perf2

This perf project is a map reduce job, written scala for Apache Spark, 
designed to take the output from kafka 
in the Clojure news feed service and prepare it for the ETL job that 
loads an OLAP cube to analyze this performance data.

This is functionally equivalent to both the NewsFeedPerformance Java project 
and the perf cascalog project.

## Usage

cd /path/to/spark/sbin

./start-all.sh

cd /path/to/perf2/project

sbt
compile
package
exit

export SPARK_HOME=/path/to/where/spark/was/installed

./run.sh /path/to/input/file /path/to/output/file

cd /path/to/spark/sbin

./stop-all.sh

## License

Copyright © 2014 Glenn Engstrand

Distributed under the Eclipse Public License.
