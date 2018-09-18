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
            view = [ row[6], row[7], row[8], row[9] ]
            data.append(view)
	    target.append(row[5])
        rownum += 1
    ifile.close()
    return [ data, target ]

clf = tree.DecisionTreeClassifier()
input = readcsv("/home/glenn/git/clojure-news-feed/client/ml/etl/throughput.csv")
data = input[0]
target = input[1]
clf = clf.fit(data, target)
dot_data = tree.export_graphviz(clf, out_file=None)
graph = graphviz.Source(dot_data) 
graph.render(view=True)
