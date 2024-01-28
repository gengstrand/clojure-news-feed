# data analysis

There is a [jupyter notebook](https://github.com/gengstrand/clojure-news-feed/blob/master/server/analysis/fc.ipynb) which explores an attempt to quantify code complexity in the various implementations. It is quite inconclusive.

There are a couple of Apache Druid [injection spec 1](https://github.com/gengstrand/clojure-news-feed/blob/master/server/analysis/feedDruidSpec.json) and [injection spec 2](https://github.com/gengstrand/clojure-news-feed/blob/master/server/analysis/feed2DruidSpec.json) in which you can explore the performance data that I have collected from the various implementations. This example assumes that you have installed Druid locally and have an appropriate version of both Java and Python installed.

```
cd path/to/local/druid
./bin/start-micro-quickstart
# wait a bit then open a new bash session
cd path/to/this/repo
curl -X 'POST' -H 'Content-Type:application/json' -d @server/analysis/feedDruidSpec.json http://localhost:8081/druid/indexer/v1/task
# wait a bit
curl -X 'POST' -H 'Content-Type:application/json' -d @server/analysis/feed2DruidSpec.json http://localhost:8081/druid/indexer/v1/task
# wait a bit
# you can also monitor the status of the ingestion at http://localhost:8888/
cd path/to/local/druid
./bin/dsql
select cloud, feed, avg(rpm) as rpm, sum(sum_duration) / sum(rpm) as avg_duration, 
APPROX_QUANTILE_DS(quantile_duration, 0.50) as p50,
APPROX_QUANTILE_DS(quantile_duration, 0.95) as p95,
APPROX_QUANTILE_DS(quantile_duration, 0.99) as p99,
avg(max_duration) as max_duration
from feed
where entity = 'outbound' and operation = 'POST'
group by cloud, feed
order by feed, cloud;

┌───────┬──────┬───────┬──────────────┬──────┬──────┬──────┬──────────────┐
│ cloud │ feed │ rpm   │ avg_duration │ p50  │ p95  │ p99  │ max_duration │
├───────┼──────┼───────┼──────────────┼──────┼──────┼──────┼──────────────┤
│ EKS   │ 1    │  4822 │           26 │ 25.0 │ 37.0 │ 44.0 │          322 │
│ GKE   │ 1    │  6316 │           19 │ 17.0 │ 33.0 │ 62.0 │          266 │
│ GKE   │ 10   │  9702 │            5 │  4.0 │  8.0 │ 13.0 │          241 │
│ GKE   │ 11   │ 14043 │            5 │  5.0 │ 11.0 │ 14.0 │          269 │
│ GKE   │ 12   │ 19941 │            4 │  4.0 │  7.0 │ 11.0 │          190 │
│ GKE   │ 13   │ 15098 │            3 │  3.0 │  6.0 │ 12.0 │          434 │
│ GKE   │ 14   │  9918 │           11 │  9.0 │ 16.0 │ 26.0 │         1114 │
│ GKE   │ 15   │  8299 │           13 │  1.0 │110.0 │184.0 │          323 │
│ EKS   │ 2    │  8030 │           13 │ 13.0 │ 18.0 │ 23.0 │          167 │
│ GKE   │ 2    │  6983 │           18 │ 13.0 │ 41.0 │ 48.0 │          292 │
│ EKS   │ 3    │ 14193 │            5 │  5.0 │  8.0 │ 11.0 │          257 │
│ GKE   │ 3    │ 18436 │            4 │  4.0 │  7.0 │ 10.0 │          213 │
│ EKS   │ 4    │ 18770 │            6 │  6.0 │  8.0 │ 11.0 │          700 │
│ GKE   │ 4    │ 13806 │            4 │  4.0 │  7.0 │ 12.0 │          152 │
│ EKS   │ 5    │  6065 │           13 │ 13.0 │ 20.0 │ 29.0 │          144 │
│ GKE   │ 5    │  9786 │            5 │  6.0 │  9.0 │ 12.0 │           54 │
│ EKS   │ 6    │  9643 │           10 │  9.0 │ 15.0 │ 21.0 │          205 │
│ GKE   │ 6    │  9580 │            9 │  9.0 │ 14.0 │ 26.0 │          231 │
│ GKE   │ 8    │  9092 │            9 │  8.0 │ 19.0 │ 33.0 │          370 │
│ GKE   │ 9    │ 14296 │            7 │  4.0 │ 21.0 │ 36.0 │          303 │
└───────┴──────┴───────┴──────────────┴──────┴──────┴──────┴──────────────┘
Retrieved 18 rows in 3.95s.
```

There are some native Druid queries for collecting the time series data for the load test runs for feeds 8, 14, and 15 on GKE. The query output is also here as CSV files. The analyze.r file has some sample R programming language scripts for analyzing and graphing the data using the tidyverse packages. There is also a python script static.py for collecting the static code analysis.