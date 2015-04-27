package info.glennengstrand.io

import java.sql.{ResultSet, PreparedStatement}
import java.util.logging.Logger

object Sql {
  val log = Logger.getLogger("info.glennengstrand.io.Sql")

  def setStatementParameterFromValue(stmt: PreparedStatement, v: Any, i: Int): Unit = {
    v match {
      case l: Long => stmt.setLong(i, v.asInstanceOf[Long])
      case s: String => stmt.setString(i, v.asInstanceOf[String])
      case _ => stmt.setString(i, v.toString)
    }
  }

  def setMapFromResultSet(f: Tuple2[String, String], d: scala.collection.mutable.Map[String, Any], rs: ResultSet): Unit = {
    f._2 match {
      case "Long" => d.put(f._1, rs.getLong(f._1))
      case _ => d.put(f._1, rs.getString(f._1))
    }
  }

  def tupleFromResultSet(f: Tuple2[String, String], rs: ResultSet): Tuple2[String, Any] = {
    val value = f._2 match {
      case "Long" => rs.getLong(f._1)
      case _ => rs.getString(f._1)
    }
    ( f._1, value )
  }

  def prepare(stmt: PreparedStatement, inputs: Iterable[String], criteria: Map[String, Any]): Unit = {
    var fii = 1
    inputs.map { f => {
      val v = criteria.get(f).getOrElse(0)
      setStatementParameterFromValue(stmt, v, fii)
      fii += 1
    }}
    log.fine(stmt.toString)
  }

  def query(stmt: PreparedStatement, outputs: Iterable[(String, String)]): Iterable[Map[String, Any]] = {
    val rs = stmt.executeQuery()
    new Iterator[Map[String, Any]] {
      def hasNext = {
        val rv = rs.next()
        rv match {
          case false => rs.close()
          case _ =>
        }
        rv
      }
      def next() = {
        outputs.map { f => {
          tupleFromResultSet(f, rs)
        }}.toMap
      }
    }.toStream
  }

  def execute(stmt: PreparedStatement, outputs: Iterable[(String, String)]): Map[_ <: String, Any] = {
    val rs = stmt.executeQuery()
    val retVal = rs.next() match {
      case true => {
        outputs.map { f => {
          tupleFromResultSet(f, rs)
        }}.toMap
      }
      case _ => {
        Map()
      }
    }
    rs.close()
    retVal
  }
}
