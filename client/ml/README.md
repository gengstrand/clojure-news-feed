# machine learning news feed performance data

Here are some assets used to collect and analyze the performance data that went into researching this blog.

http://glennengstrand.info/software/performance/eks/gke

## Extract, Transform, and Load

The etl folder contains scripts used to extract the performance data from elasticsearch, convert it to a format that is more appropriate for machine learning algorithms, and write aggregated CSV files for throughput and latency.

After running each load test, you can extract the performance data out of elasticsearch with this command.

```bash
python extract.py feed cloud elasticsearch >rawdata.csv
```

Where

feed is am integer number between 1 and 6 inclusive that indicates which feed implementation was tested
cloud is either EKS or GKE which indicates which cloud was tested
elasticsearch is the host name or IP address of the server where elasticsearch is running

Once you have collected all of the data, you can then prepare it by running this command.

```bash
sh prepare.sh path/to/data
```

The path/to/data is the root folder where all the raw data fetched by extract.py was collected.

## Decision Trees

The dt folder contains the scripts used to analyze the data collected by the assets in the etl folder.

There are assets here that analyze the data using decision trees from the R programming language, scikit-learn, and Spark MLlib.
