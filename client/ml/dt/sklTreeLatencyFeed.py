import graphviz
import csv
from sklearn import tree

def readcsv(filename):	
    ifile = open(filename, "rU")
    reader = csv.reader(ifile, delimiter=",")
    rownum = 0	
    data = []
    target = []
    for row in reader:
        if rownum > 0:
            cloud = 0.0
            if row[5] == "GKE":
                cloud = 1.0
            view = [ cloud, row[7], row[8], row[9] ]
            data.append(view)
	    target.append(row[6])
        rownum += 1
    ifile.close()
    return [ data, target ]

clf = tree.DecisionTreeClassifier()
input = readcsv("/home/glenn/git/clojure-news-feed/client/ml/etl/latency.csv")
data = input[0]
target = input[1]
clf = clf.fit(data, target)
dot_data = tree.export_graphviz(clf, out_file=None)
graph = graphviz.Source(dot_data) 
graph.render(view=True)
