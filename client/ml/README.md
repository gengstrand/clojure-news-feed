# machine learning news feed performance data

Here are some assets used to collect and analyze the performance data that went into researching this blog.

http://glennengstrand.info/software/performance/eks/gke

You can learn more about how these scripts were used here.

http://glennengstrand.info/software/architecture/msa/ml

http://glennengstrand.info/software/architecture/ml/oss

The ml.ipynb file is a Jupyter notebook for the scikit-learn programs.

## Extract, Transform, and Load

The etl folder contains scripts used to extract the performance data from elasticsearch, convert it to a format that is more appropriate for machine learning algorithms, and write aggregated CSV files for throughput and latency.

After running each load test, you can extract the performance data out of elasticsearch with this command.

```bash
pip install python-dateutil
pip install elasticsearch
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

There are programs here that analyze the data using decision trees from the R programming language, tensorflow, scikit-learn, and Spark MLlib.

There are hard coded folders and file names in these scripts so you will need to edit these files before running them.

I used the clipboard and spark-shell for the scala files. I did something similar for the R program.

There is also a jupyter notebook which compares the accuracy between scikit-learn and tensorflow.

You will need to install some libraries for the python programs before running them.

```bash
pip install --upgrade --user tensorflow
pip install numpy
pip install scikit-learn
pip install pandas
pip install matplotlib
```

I ran these programs with python 2.7 on Ubuntu Bionic Beaver.
