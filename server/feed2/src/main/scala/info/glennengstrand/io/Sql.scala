package info.glennengstrand.io

import java.sql.{ResultSet, PreparedStatement}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.mutable.ArrayBuffer

/** JDBC helper functions */
object Sql {
  val log = LoggerFactory.getLogger("info.glennengstrand.io.Sql")

  /** call the right statement setter based on value type */
  def setStatementParameterFromValue(stmt: PreparedStatement, v: Any, i: Int): Unit = {
    v match {
      case l: Long => stmt.setLong(i, l)
      case iv: Int => stmt.setInt(i, iv)
      case d: java.util.Date => stmt.setDate(i, new java.sql.Date(d.getTime))
      case s: String => stmt.setString(i, s)
      case _ => stmt.setString(i, v.toString)
    }
  }

  /** generate a map of column name, value calling the right result set getter based on column type */
  def setMapFromResultSet(f: Tuple2[String, String], d: scala.collection.mutable.Map[String, Any], rs: ResultSet): Unit = {
    f._2 match {
      case "Long" => d.put(f._1, rs.getLong(f._1))
      case "Int" => d.put(f._1, rs.getInt(f._1))
      case "Date" => d.put(f._1, rs.getDate(f._1))
      case _ => d.put(f._1, rs.getString(f._1))
    }
  }

  /** prepare a column name, value tuple calling the right result set getter based on column type */
  def tupleFromResultSet(f: Tuple2[String, String], rs: ResultSet): Tuple2[String, Any] = {
    val value = f._2 match {
      case "Long" => rs.getLong(f._1)
      case "Date" => rs.getDate(f._1)
      case "Int" => rs.getInt(f._1)
      case _ => rs.getString(f._1)
    }
    ( f._1, value )
  }

  /** set the parameters of the prepared statement */
  def prepare(stmt: PreparedStatement, inputs: Iterable[String], criteria: Map[String, Any]): Unit = {
    var fii = 1
    inputs.map { f => {
      val v = criteria.get(f).getOrElse(0)
      setStatementParameterFromValue(stmt, v, fii)
      fii += 1
    }}
    log.debug(stmt.toString)
  }

  /** execute the query statement and wrap the returning result set in an iterator */
  def query(stmt: PreparedStatement, outputs: Iterable[(String, String)]): Iterable[Map[String, Any]] = {
    val rs = stmt.executeQuery()
    val retVal = new ArrayBuffer[Map[String, Any]]()
    while (rs.next()) {
      retVal.append(outputs.map(f => tupleFromResultSet(f, rs)).toMap)
    }
    rs.close()
    log.debug(s"query returned ${retVal.size} results")
    retVal
  }

  /** execute the upsert statement expecting a single row of output */
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
