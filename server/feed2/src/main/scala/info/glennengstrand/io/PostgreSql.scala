package info.glennengstrand.io

import java.util.logging.Logger

import info.glennengstrand.io._
import java.sql.{SQLException, ResultSet, PreparedStatement, Connection}

import scala.collection.mutable

object PostgreSql {
  val log = Logger.getLogger("info.glennengstrand.io.PostgreSql")
  val sql: scala.collection.mutable.Map[String, PreparedStatement] = scala.collection.mutable.Map()
  def reset: Unit = {
    sql.synchronized {
      sql.clear()
    }
  }
  def prepare(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)], pool: PooledRelationalDataStore): PreparedStatement = {
    val key = operation + ":" + entity
    sql.contains(key) match {
      case false => {
        sql.synchronized {
          sql.contains(key) match {
            case false => {
              val i = for (x <- inputs) yield "?"
              val o = for ((fn, ft) <- outputs) yield fn
              val stmt = "select " + o.reduce(_ + "," + _) + " from " + operation + entity + "(" + i.reduce(_ + "," + _) + ")"
              val retVal = pool.getDbConnection.prepareStatement(stmt)
              sql.put(key, retVal)
              retVal
            }
            case true => sql.get(key).get
          }
        }
      }
      case true => sql.get(key).get
    }
  }
}
class PostgreSqlReader extends PersistentRelationalDataStoreReader {
  val fetch: String = "Fetch"
  def reset: Unit = {
    PostgreSql.reset
  }
  def prepare(entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)], pool: PooledRelationalDataStore): PreparedStatement = {
    PostgreSql.prepare(fetch, entity, inputs, outputs, pool)
  }
}

trait PostgreSqlWriter extends PersistentRelationalDataStoreWriter {
  val upsert: String = "Upsert"
  def reset: Unit = {
    PostgreSql.reset
  }
  def prepare(entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)], pool: PooledRelationalDataStore): PreparedStatement = {
    PostgreSql.prepare(upsert, entity, inputs, outputs, pool)
  }
}

