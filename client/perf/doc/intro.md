# Introduction to perf

The perf project is a Cascalog map reduce job functionally identical 
to the java based NewsFeedPerformance project.

Cascalog is a library for writing hadoop map reduce jobs in Clojure.

It is built on top of the Cascading platform.

http://cascalog.org/

http://www.cascading.org/

Cascalog has an extensive library of built in functions that make it easy to write
queries with the same level of expressiveness as Hive. Here is a sample query that
inputs the output from our kafka performance log of the Clojure news feed service
and generates the sum of the performance data grouped by time, entity, and action
down to the minute granularity.

(defn roll-up-by-minute-entity-action 
  "select year, month, day, hour, minute, entity, action, sum(count) group by year, month, day, hour, minute, entity, action"
  [input-directory output-directory]
  (let [data-point (metrics input-directory)
        output (hfs-delimited output-directory :sinkmode :replace :delimiter ",")]
       (c/?<- output 
              [?year ?month ?day ?hour ?minute ?entity ?action ?total] 
              (data-point ?year ?month ?day ?hour ?minute ?entity ?action ?count) 
              (parse-int ?count :> ?cnt) 
              (o/sum ?cnt :> ?total))))
            
By coding your own custom aggregators, you can write even more expressive queries
with full control of the reduce side processing. That is what this project attempted 
to do by duplicating the functionality of the map reduce job, written in Java, from 
the NewsFeedPerformance folder.

https://github.com/nathanmarz/cascalog/wiki/Guide-to-custom-operations

The results were not very impressive. I had to print the input tuples in the custom aggregator 
in order to get the proper results. Otherwise, the cardinality of the tuples would be
correct but all the data points would be identical. That suggests to me that Cascalog
is not as true to Functional Programming as Clojure is.

This 109 line program processes the same 4077809 rows of raw performance data as the 
144 line program in the NewsFeedPerformance project and generates the same 122 lines
of output but takes four times as long to do the processing.
