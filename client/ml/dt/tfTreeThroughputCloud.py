import csv
import tensorflow as tf
import numpy as np
from tensorflow.python.ops import parsing_ops
from tensorflow.contrib.tensor_forest.python import tensor_forest
from tensorflow.contrib.learn.python.learn.utils import input_fn_utils

def readcsv(filename):	
    ifile = open(filename, "rU")
    reader = csv.reader(ifile, delimiter=",")
    rownum = 0	
    data = []
    target = []
    for row in reader:
        if rownum > 0:
            view = [ row[7], row[8], row[9] ]
            data.append(view)
            cloud = 0
            if row[5] == 'EKS':
                cloud = 1
	    target.append(cloud)
        rownum += 1
    ifile.close()
    return [ data, target ]

input = readcsv("/home/glenn/git/clojure-news-feed/client/ml/etl/throughput.csv")
data = np.array(input[0], dtype=np.float32)
target = np.array(input[1], dtype=np.float32)
hparams = tensor_forest.ForestHParams(num_classes=2,
                                      num_features=3,
                                      num_trees=1,
                                      regression=False,
                                      max_nodes=500).fill()
classifier = tf.contrib.tensor_forest.client.random_forest.TensorForestEstimator(hparams, model_dir="/home/glenn/git/clojure-news-feed/client/ml/dt/e1")
feature_spec = {"friends": parsing_ops.FixedLenFeature([1], dtype=tf.float32),
                "outbound": parsing_ops.FixedLenFeature([1], dtype=tf.float32),
                "participant": parsing_ops.FixedLenFeature([1], dtype=tf.float32)}
serving_input_fn = input_fn_utils.build_parsing_serving_input_fn(feature_spec)
c = classifier.fit(x=data, y=target)
c.export_savedmodel("/home/glenn/git/clojure-news-feed/client/ml/dt/export", serving_input_fn)
print c.evaluate(x=data, y=target)
