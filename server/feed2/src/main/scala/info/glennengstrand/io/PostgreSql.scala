package info.glennengstrand.io

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.glennengstrand.io._
import java.sql.{SQLException, ResultSet, PreparedStatement, Connection}

import scala.collection.mutable

/** helper functions for generating postgesql statements */
object PostgreSql {
  val log = LoggerFactory.getLogger("info.glennengstrand.io.PostgreSql")

  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    val i = for (x <- inputs) yield "?"
    val o = for ((fn, ft) <- outputs) yield fn
    "select " + o.reduce(_ + "," + _) + " from " + operation + entity + "(" + i.reduce(_ + "," + _) + ")"
  }

}

/** postgresql specific reader */
class PostgreSqlReader extends PersistentRelationalDataStoreReader {
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    PostgreSql.generatePreparedStatement(operation, entity, inputs, outputs)
  }
}

/** postgresql specific writer */
trait PostgreSqlWriter extends PersistentRelationalDataStoreWriter {
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String = {
    PostgreSql.generatePreparedStatement(operation, entity, inputs, outputs)
  }
}

