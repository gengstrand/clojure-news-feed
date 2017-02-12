# The Making of Elasticsearch vs Solr

One of the sources of performance data was the kafka logs on how long each request took in milliseconds. You can capture those logs from kafka to a text file like this.

```bash
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic feed --from-beginning >esperf.log
```

For inserts, we analyzed the output post data like this.

```bash
grep 'outbound|post' <esperf.log | tr '|' ',' | sed -e '1,$s/,/-/' -e '1,$s/,/-/' -e '1,$s/,/-/' -e '1,$s/,/-/' | cut -d ',' -f 1,4 >esoutbound.csv
grep 'outbound|post' <solrperf.log | tr '|' ',' | sed -e '1,$s/,/-/' -e '1,$s/,/-/' -e '1,$s/,/-/' -e '1,$s/,/-/' | cut -d ',' -f 1,4 >solroutbound.csv
```

We did something similar for analyzing searches.

```bash
grep 'outbound|search' <esperf.log | tr '|' ',' | sed -e '1,$s/,/-/' -e '1,$s/,/-/' -e '1,$s/,/-/' -e '1,$s/,/-/' | cut -d ',' -f 1,4 >esoutboundsearch.csv
grep 'outbound|search' <solrperf.log | tr '|' ',' | sed -e '1,$s/,/-/' -e '1,$s/,/-/' -e '1,$s/,/-/' -e '1,$s/,/-/' | cut -d ',' -f 1,4 >solroutboundsearch.csv
```

I used some R scripts to anaylze the kafka data. This script is for the insert tests and I ran something very similar for the search tests.

```R
es <- read.csv("/home/glenn/Documents/aws/jan28/esoutbound.csv")
esc <- aggregate(duration ~ ts, es, length)
esd <- aggregate(duration ~ ts, es, mean)
ess <- aggregate(duration ~ ts, es, summary)
s <- read.csv("/home/glenn/Documents/aws/jan28/solroutbound.csv")
sc <- aggregate(duration ~ ts, s, length)
sd <- aggregate(duration ~ ts, s, mean)
ss <- aggregate(duration ~ ts, s, summary)
rc <- range(0, esc$duration, sc$duration)
plot(esc$duration, type="o", col="blue", ylim=rc, axes=FALSE, ann=FALSE)
lines(sc$duration, type="o", col="red")
axis(2, las=1, at=4000*0:rc[2])
legend(1, rc[2], c("elastic", "solr"), cex=0.8, col=c("blue", "red"), pch=21:22, lty=1:2)
title(ylab="throughput")
title(xlab="time")
title(main="per minute throughput")
box()
rd <- range(0, esd$duration, sd$duration)
plot(esd$duration, type="o", col="blue", ylim=rd, axes=FALSE, ann=FALSE)
lines(sd$duration, type="o", col="red")
axis(2, las=1, at=100*0:rd[2])
legend(1, rd[2], c("elastic", "solr"), cex=0.8, col=c("blue", "red"), pch=21:22, lty=1:2)
title(ylab="average (ms)")
title(xlab="time")
title(main="per minute latency")
box()
```

The other source of data was to collect metrics from each technology's statistics plugin. That is what this perf5 project is all about. See the README.md file on how to run this command line utility.

```bash
java -jar perf5-0.1.0-standalone.jar solr 127.0.0.1 8983 solr/outbound 120 60000 >solrperfinsert.csv
```

This R script analyzes the output from the insert doc test runs. I ran something similar for the search test runs.

```R
es <- read.csv("/home/glenn/Documents/aws/jan28/esperfinsert.csv")
s <- read.csv("/home/glenn/Documents/aws/jan28/solrperfinsert.csv")
rc <- range(0, es$index_total, s$index_total)
plot(es$index_total, type="o", col="blue", ylim=rc, axes=FALSE, ann=FALSE)
lines(s$index_total, type="o", col="red")
axis(2, las=1, at=5000*0:rc[2])
legend(1, rc[2], c("elastic", "solr"), cex=0.8, col=c("blue", "red"), pch=21:22, lty=1:2)
title(ylab="throughput")
title(xlab="time")
title(main="per minute index throughput")
box()
```

