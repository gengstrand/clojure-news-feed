package perf

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import scala.util.Sorting

object NewsFeedPerformance {

    def main(args: Array[String]) {
      val logFile = "/home/glenn/Documents/newsFeedRawMetrics/perfpostgres.csv"
      val conf = new SparkConf().setAppName("News Feed Performance")
      conf.setMaster("spark://glenn-laptop:7077")
      val sc = new SparkContext(conf)
      val perfData = sc.textFile(logFile)
      perfData.map((line: String) => {
        val data = line.split(",")
        ( data.slice(0, 5).mkString(","), data.slice(5, 8).mkString(",") )
      })
      .groupByKey()
      .map {
        case (ts, metrics) => {
          val tsray = ts.split(",")
          val tskey = tsray(0) + "-" + tsray(1) + "-" + tsray(2) + " " + tsray(3) + ":" + tsray(4)
          val names: Iterable[String] = for (metric <- metrics) yield metric.split(",").slice(0, 2).mkString("|")
          val values: Stream[String] = names.toStream.distinct.map { (name) => {
            val latencies = for (statistic <- metrics.filter((metric) => metric.split(",").slice(0, 2).mkString("|") == name))
              yield statistic.split(",").slice(2, 3).mkString.toInt
            val statistics = latencies.toArray
            Sorting.stableSort(statistics)
            name + "=" + statistics.length.toString + "," + statistics(statistics.length / 2).toString + "," + statistics(statistics.length * 95 / 100).toString
          }}
          ( tskey, values.mkString(":"))
        }
      }
      .saveAsTextFile("/tmp/perf")
    }
}
