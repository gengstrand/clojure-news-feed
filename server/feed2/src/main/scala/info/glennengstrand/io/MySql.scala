package info.glennengstrand.io

import info.glennengstrand.io._
import java.sql.{ResultSet, PreparedStatement, Connection}

import scala.collection.mutable

object MySql {
  val sql: scala.collection.mutable.Map[String, PreparedStatement] = scala.collection.mutable.Map()

  def prepare(operation: String, entity: String, inputs: Iterable[String], db: Connection): PreparedStatement = {
    val key = operation + ":" + entity
    sql.contains(key) match {
      case false => {
        sql.synchronized {
          sql.contains(key) match {
            case false => {
              val i = for (x <- inputs) yield "?"
              val select = "{ call " + operation + entity + "(" + i.reduce(_ + "," + _) + ") }"
              val retVal = db.prepareStatement(select)
              sql.put(key, retVal)
              retVal
            }
            case true => sql.get(operation).get
          }
        }
      }
      case true => sql.get(operation).get
    }
  }

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

  def prepare(stmt: PreparedStatement, inputs: Iterable[String], criteria: Map[String, Any]): Unit = {
    var fii = 1
    inputs.map { f => {
      val v = criteria.get(f).getOrElse(0)
      MySql.setStatementParameterFromValue(stmt, v, fii)
      fii += 1
    }}
  }

  def execute(stmt: PreparedStatement, outputs: Iterable[(String, String)]): mutable.Map[_ <: String, _] = {
    val rs = stmt.executeQuery()
    val retVal = rs.next() match {
      case true => {
        val d: scala.collection.mutable.Map[String, Any] = scala.collection.mutable.Map()
        outputs.map { f => {
          MySql.setMapFromResultSet(f, d, rs)
        }}
        d
      }
      case false => {
        scala.collection.mutable.Map()
      }
    }
    rs.close()
    retVal
  }
}

class MySqlReader extends PersistentDataStoreReader with PooledRelationalDataStore {
  val vendor: String = "mysql"
  val fetch: String = "Fetch"
  lazy val db: Connection = getDbConnection

  def read(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Map[String, Any] = {
    val stmt = MySql.prepare(fetch, o.entity, o.fetchInputs, db)
    MySql.prepare(stmt, o.fetchInputs, criteria)
    MySql.execute(stmt, o.fetchOutputs).toMap[String, Any]
  }
}

trait MySqlWriter extends PersistentDataStoreWriter with PooledRelationalDataStore {
  val vendor: String = "mysql"
  val upsert: String = "Upsert"
  lazy val db: Connection = getDbConnection
  def write(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Map[String, Any] = {
    val stmt = MySql.prepare(upsert, o.entity, o.upsertInputs, db)
    MySql.prepare(stmt, o.upsertInputs, criteria)
    MySql.execute(stmt, o.upsertOutputs).toMap[String, Any]
  }
}