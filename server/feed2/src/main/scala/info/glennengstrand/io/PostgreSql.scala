package info.glennengstrand.io

import java.util.logging.Logger

import info.glennengstrand.io._
import java.sql.{SQLException, ResultSet, PreparedStatement, Connection}

import scala.collection.mutable

object PostgreSql {
  val log = Logger.getLogger("info.glennengstrand.io.PostgreSql")

  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    val i = for (x <- inputs) yield "?"
    val o = for ((fn, ft) <- outputs) yield fn
    "select " + o.reduce(_ + "," + _) + " from " + operation + entity + "(" + i.reduce(_ + "," + _) + ")"
  }

}
class PostgreSqlReader extends PersistentRelationalDataStoreReader {
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    PostgreSql.generatePreparedStatement(operation, entity, inputs, outputs)
  }
}

trait PostgreSqlWriter extends PersistentRelationalDataStoreWriter {
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    PostgreSql.generatePreparedStatement(operation, entity, inputs, outputs)
  }
}

