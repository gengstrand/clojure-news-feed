import tensorflow as tf
import numpy as np
import pandas
from tensorflow.python.ops import parsing_ops
from tensorflow.contrib.tensor_forest.python import tensor_forest
from tensorflow.contrib.learn.python.learn.utils import input_fn_utils
from sklearn.model_selection import train_test_split

input = pandas.read_csv("/home/glenn/git/clojure-news-feed/client/ml/etl/throughput.csv")
data = input[input.columns[6:9]]
target = input['cloud'].apply(lambda x: 1.0 if x == 'GKE' else 0.0)
X_train, X_test, y_train, y_test = train_test_split(data, target, test_size=0.4, random_state=0)
X_train_np = np.array(X_train, dtype=np.float32)
y_train_np = np.array(y_train, dtype=np.int32)
X_test_np = np.array(X_test, dtype=np.float32)
y_test_np = np.array(y_test, dtype=np.int32)
hparams = tensor_forest.ForestHParams(num_classes=3,
                                      num_features=3,
                                      num_trees=1,
                                      regression=False,
                                      max_nodes=500).fill()
classifier = tf.contrib.tensor_forest.client.random_forest.TensorForestEstimator(hparams)
c = classifier.fit(x=X_train_np, y=y_train_np)
print c.evaluate(x=X_test_np, y=y_test_np)
