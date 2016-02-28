package info.glennengstrand.perf3

import akka.actor.Actor
import scala.collection.mutable.ArrayBuffer

/** factory method for a performance measurement which parses the kafka consumer record value */
object PerformanceMeasurement {
  def apply(msg: String): PerformanceMeasurement = {
    val parts = msg.split("\\|")
    val year = parts(0).toInt
    val month = parts(1).toInt
    val day = parts(2).toInt
    val hour = parts(3).toInt
    val minute = parts(4).toInt
    val entity = parts(5)
    val operation = parts(6)
    val duration = parts(7).toLong
    PerformanceMeasurement(year, month, day, hour, minute, entity, operation, duration)
  }
}

/** represents a performance measurement as consumed from the kafka feed topic */
case class PerformanceMeasurement(year: Int, month: Int, day: Int, hour: Int, minute: Int, entity: String, operation: String, duration: Long) {
  def key = s"${entity}|${operation}"
  override def toString: String = {
    val retVal = new StringBuilder()
    retVal.append("{\"year\":")
    retVal.append(year.toString())
    retVal.append(",\"month\":")
    retVal.append(month.toString())
    retVal.append(",\"day\":")
    retVal.append(day.toString())
    retVal.append(",\"hour\":")
    retVal.append(hour.toString())
    retVal.append(",\"minute\":")
    retVal.append(minute.toString())
    retVal.append(",\"entity\":\"")
    retVal.append(entity)
    retVal.append("\",\"operation\":\"")
    retVal.append(operation)
    retVal.append("\",\"duration\":")
    retVal.append(duration.toString())
    retVal.append("}")
    retVal.toString()
  }
}

/** represents the request to aggregate the performance events collected so far and report the aggregate metrics to elastic search */
case class MetricAggregationEvent(ts: String) 


/** accumulates performance measurements then periodically aggregates those measurements into a metric for elastic search */
class PerformanceMetric(entity: String, operation: String) extends Actor {
  val measurements: ArrayBuffer[PerformanceMeasurement] = ArrayBuffer.empty[PerformanceMeasurement]
  def receive = {
    case measurement: PerformanceMeasurement => {
      measurements += measurement
    }
    case event: MetricAggregationEvent => {
      measurements.size match {
        case 0 => {
          Main.elasticSearchActor ! PerformanceMetricUpdate(event.ts, entity, operation, 0, 0L, 0L, 0L)
        }
        case _ => {
          val m = measurements.sortBy { metric => metric.duration }
          val throughput = m.size
          val mean = m.map(m => m.duration).sum / throughput
          val median = m(throughput / 2).duration
          val upper5 = m(throughput * 95 / 100).duration
          Main.elasticSearchActor ! PerformanceMetricUpdate(event.ts, entity, operation, throughput, mean, median, upper5)
        }
      }
      measurements.clear()
    }
  }
}