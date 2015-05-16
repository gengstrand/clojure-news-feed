package info.glennengstrand.io

import java.util.logging.{Level, Logger}

import info.glennengstrand.io._
import java.sql.{ResultSet, PreparedStatement, Connection, SQLException}

import scala.collection.mutable

/** MySql specific helper functions */
object MySql {
  val log = Logger.getLogger("info.glennengstrand.io.MySql")
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    val i = for (x <- inputs) yield "?"
    val retVal = "{ call " + operation + entity + "(" + i.reduce(_ + "," + _) + ") }"
    log.finest("preparing: " + retVal)
    retVal
  }
}

/** MySql specific reader */
class MySqlReader extends PersistentRelationalDataStoreReader  {
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    MySql.generatePreparedStatement(operation, entity, inputs, outputs)
  }
}

/** MySql specific writer */
trait MySqlWriter extends PersistentRelationalDataStoreWriter {
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    MySql.generatePreparedStatement(operation, entity, inputs, outputs)
  }
}
