package info.glennengstrand.io

import java.util.logging.{Level, Logger}

import info.glennengstrand.io._
import java.sql.{ResultSet, PreparedStatement, Connection, SQLException}

import scala.collection.mutable

object MySql {
  val log = Logger.getLogger("info.glennengstrand.io.MySql")
  val CACHE_STATEMENTS = true
  val sql: scala.collection.mutable.Map[String, PreparedStatement] = scala.collection.mutable.Map()

  def reset: Unit = {
    sql.synchronized {
      sql.clear()
    }
  }
  def prepare(operation: String, entity: String, inputs: Iterable[String], pool: PooledRelationalDataStore): PreparedStatement = {
    def createStatement(i: Iterable[String]): PreparedStatement = {
      val callProcedure = "{ call " + operation + entity + "(" + i.reduce(_ + "," + _) + ") }"
      pool.getDbConnection.prepareStatement(callProcedure)
    }
    if (CACHE_STATEMENTS) {
      val key = operation + ":" + entity
      sql.contains(key) match {
        case false => {
          sql.synchronized {
            sql.contains(key) match {
              case false => {
                val i = for (x <- inputs) yield "?"
                val retVal = createStatement(i)
                sql.put(key, retVal)
                retVal
              }
              case true => sql.get(key).get
            }
          }
        }
        case true => sql.get(key).get
      }
    } else {
      val i = for (x <- inputs) yield "?"
      createStatement(i)
    }
  }
}

class MySqlReader extends PersistentRelationalDataStoreReader  {
  val fetch: String = "Fetch"
  def reset: Unit = {
    MySql.reset
  }
  def prepare(entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)], pool: PooledRelationalDataStore): PreparedStatement = {
    MySql.prepare(fetch, entity, inputs, pool)
  }
}

trait MySqlWriter extends PersistentRelationalDataStoreWriter {
  val upsert: String = "Upsert"
  def reset: Unit = {
    MySql.reset
  }
  def prepare(entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)], pool: PooledRelationalDataStore): PreparedStatement = {
    MySql.prepare(upsert, entity, inputs, pool)
  }
}