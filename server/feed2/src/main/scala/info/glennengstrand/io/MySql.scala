package info.glennengstrand.io

import java.util.logging.Logger

import info.glennengstrand.io._
import java.sql.{ResultSet, PreparedStatement, Connection}

import scala.collection.mutable

object MySql {
  val log = Logger.getLogger("info.glennengstrand.io.MySql")
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
            case true => sql.get(key).get
          }
        }
      }
      case true => sql.get(key).get
    }
  }

}

class MySqlReader extends PersistentDataStoreReader with PooledRelationalDataStore {
  val fetch: String = "Fetch"
  lazy val db: Connection = getDbConnection

  def read(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]] = {
    val stmt = MySql.prepare(fetch, o.entity, o.fetchInputs, db)
    Sql.prepare(stmt, o.fetchInputs, criteria)
    Sql.query(stmt, o.fetchOutputs)
  }
}

trait MySqlWriter extends PersistentDataStoreWriter with PooledRelationalDataStore {
  val upsert: String = "Upsert"
  lazy val db: Connection = getDbConnection
  def write(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Map[String, Any] = {
    val stmt = MySql.prepare(upsert, o.entity, o.upsertInputs, db)
    Sql.prepare(stmt, o.upsertInputs, state)
    Sql.execute(stmt, o.upsertOutputs).toMap[String, Any]
  }
}