
INPUT_FILE=$1
OUTPUT_FILE=$2
TEMP_OUTPUT_FOLDER=/tmp/perf`date +%Y%m%d%H%M`
rm -Rf $TEMP_OUTPUT_FOLDER

CLASSPATH=`echo "show full-classpath" | sbt | grep 'Attributed' | sed -e '1,$s/^.*List.Attributed.//' -e '1,$s/., Attributed./,/g' -e '1,$s/classes/perf2_2.10-1.0.jar/' | cut -d ')' -f 1`
PERF2_HOME=`pwd`
cd $SPARK_HOME
MASTER=`grep 'Starting Spark master at' logs/*.out | grep -o 'spark:.*$'`
cd $PERF2_HOME

$SPARK_HOME/bin/spark-submit --jars $CLASSPATH --class perf2.NewsFeedPerformance target/scala-2.10/perf2_2.10-1.0.jar $INPUT_FILE $MASTER $TEMP_OUTPUT_FOLDER

cat $TEMP_OUTPUT_FOLDER/part-* | tr -d '(' | tr -d ')' | sed -e '1,$s/,/\t/' >$OUTPUT_FILE







