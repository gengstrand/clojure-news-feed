import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
val hr = sc.textFile("/home/glenn/git/clojure-news-feed/client/ml/etl/throughput.csv")
val header = hr.first()
val records = hr.filter(r => !r.equals(header)).map(line => line.split(","))
val data = records.map { r =>
    val label = r(6).toDouble
    val features = r.slice(7, 9).map(d => d.toDouble)
    LabeledPoint(label, Vectors.dense(features))
}
val splits = data.randomSplit(Array(0.7, 0.3))
val (trainingData, testData) = (splits(0), splits(1))
val numClasses = 7
val categoricalFeaturesInfo = Map[Int, Int]()
val impurity = "gini"
val maxDepth = 6
val maxBins = 32
val model = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo, impurity, maxDepth, maxBins)
println(model.toDebugString)
