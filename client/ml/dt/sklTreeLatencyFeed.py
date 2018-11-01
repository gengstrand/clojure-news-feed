import graphviz
import pandas
from sklearn import tree

clf = tree.DecisionTreeClassifier()
input = pandas.read_csv("/home/glenn/git/clojure-news-feed/client/ml/etl/latency.csv")
data = input[input.columns[7:9]]
data['cloud'] = input['cloud'].apply(lambda x: 1.0 if x == 'GKE' else 0.0)
target = input['feed']
clf = clf.fit(data, target)
dot_data = tree.export_graphviz(clf, out_file=None)
graph = graphviz.Source(dot_data) 
graph.render(view=True)
