import graphviz
import pandas
from sklearn import tree

clf = tree.DecisionTreeClassifier()
input = pandas.read_csv("/home/glenn/git/clojure-news-feed/client/ml/etl/throughput.csv")
data = input[input.columns[6:9]]
target = input['cloud']
clf = clf.fit(data, target)
dot_data = tree.export_graphviz(clf, out_file=None)
graph = graphviz.Source(dot_data) 
graph.render(view=True)
