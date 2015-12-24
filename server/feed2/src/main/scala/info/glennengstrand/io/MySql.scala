package info.glennengstrand.io

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.glennengstrand.io._
import java.sql.{ResultSet, PreparedStatement, Connection, SQLException}

import scala.collection.mutable

/** MySql specific helper functions */
object MySql {
  val log = LoggerFactory.getLogger("info.glennengstrand.io.MySql")
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    val i = for (x <- inputs) yield "?"
    val retVal = "{ call " + operation + entity + "(" + i.reduce(_ + "," + _) + ") }"
    log.debug("preparing: " + retVal)
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
