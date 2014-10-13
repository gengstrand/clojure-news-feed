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

review the log output and edit the scala file src/main/scala/perf/NewsFeedPerformance.scala to set the correct master URL

cd /path/to/perf2/project

sbt

compile
package
show full-classpath

replace CLASSPATH data with the output from that show command. 
In the end it should be a comma delimited string of jar files.

rm -Rf /path/to/output/folder

./run.sh

## License

Copyright © 2014 Glenn Engstrand

Distributed under the Eclipse Public License, the same as Clojure.
