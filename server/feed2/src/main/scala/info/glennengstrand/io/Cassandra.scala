package info.glennengstrand.io

import com.datastax.driver.core._

/**
 * Created by glenn on 2/28/15.
 * http://www.datastax.com/documentation/developer/java-driver/1.0/java-driver/quick_start/qsSimpleClientAddSession_t.html
 */

object Cassandra {
  val sql: scala.collection.mutable.Map[String, PreparedStatement] = scala.collection.mutable.Map()

  def getConsistencyLevel(level: String): ConsistencyLevel = {
    level match {
      case "one" => ConsistencyLevel.ONE
      case "quorum" => ConsistencyLevel.LOCAL_QUORUM
      case _ => ConsistencyLevel.ANY
    }
  }
  def getCluster: Cluster = {
    // TODO: host could also be a comma delimiter list of server and port
    val b = Cluster.builder().addContactPoint(IO.settings.get(IO.nosqlHost).asInstanceOf[String])
    b.build()
  }
  def getKey(operation: String, entity: String): String = {
    operation + ":" + entity
  }
  lazy val cluster = getCluster
  lazy val session = cluster.connect(IO.settings.get(IO.nosqlKeyspace).asInstanceOf[String])
  def prepareFetch(operation: String, o: PersistentDataStoreBindings): PreparedStatement = {
    val key = getKey(operation, o.entity)
    sql.contains(key) match {
      case false => {
        sql.synchronized {
          sql.contains(key) match {
            case false => {
              val sl = for (f <- o.fetchOutputs) yield f._1
              val wl = for (f <- o.fetchInputs) yield  f + " = ?"
              val fo = o.fetchOrder
              val ol = for (k <- fo.keys) yield k + " " + fo.get(k)
              val select = "select " + sl.reduce(_ + ", " + _) + " from " + o.entity + " where " + wl.reduce(_ + " and " + _) + " order by " + ol.reduce(_ + ", " + _)
              val stmt = session.prepare(select)
              val cl = getConsistencyLevel(IO.settings.get(IO.nosqlReadConsistencyLevel).asInstanceOf[String])
              stmt.setConsistencyLevel(cl)
              sql.put(operation, stmt)
              stmt
            }
            case true => sql.get(key).get
          }
        }
      }
      case true => sql.get(key).get
    }
  }
  def prepareUpsert(operation: String, o: PersistentDataStoreBindings): PreparedStatement = {
    val key = getKey(operation, o.entity)
    sql.contains(key) match {
      case false => {
        sql.synchronized {
          sql.contains(key) match {
            case false => {
              val vl = for (f <- o.upsertInputs) yield "?"
              val fl = for (f <- o.upsertInputs) yield  f
              // TODO: now() and  using ttl
              val upsert = "insert into " + o.entity + "("  + fl.reduce(_ + ", " + _) + ") values (" + vl.reduce(_ + ", " + _) + ")"
              val stmt = session.prepare(upsert)
              val cl = getConsistencyLevel(IO.settings.get(IO.nosqlReadConsistencyLevel).asInstanceOf[String])
              stmt.setConsistencyLevel(cl)
              sql.put(operation, stmt)
              stmt
            }
            case true => sql.get(key).get
          }
        }
      }
      case true => sql.get(key).get
    }
  }
  def setBinding(binding: BoundStatement, fieldName: String, state: Map[String, Any], index: Int): Unit = {
    state.get(fieldName).getOrElse(0) match {
      case l: Long => binding.setLong(index, l)
      case s: String => binding.setString(index, s)
    }
  }
  def mapResults(retVal: scala.collection.mutable.Map[String, Any], r: Row, field: Tuple2[String, String]): Unit = {
    val fn = field._1.asInstanceOf[String]
    field._2 match {
      case "Long" => retVal.put(fn, r.getLong(fn))
      case _ => retVal.put(fn, r.getString(fn))
    }
  }
  def tupleFromRow(f: Tuple2[String, String], r: Row): Tuple2[String, Any] = {
    f._2 match {
      case "Long" => (f._1, r.getLong(f._1))
      case _ => (f._1, r.getString(f._1))
    }
  }
  def bindInputs(stmt: PreparedStatement, o: PersistentDataStoreBindings, criteria: Map[String, Any]): BoundStatement = {
    val retVal = new BoundStatement(stmt)
    var index = 1
    o.fetchInputs.foreach(f => {
      setBinding(retVal, f, criteria, index)
      index += 1
    })
    retVal
  }
  def bindSingleRowOutputs(binding: BoundStatement, o: PersistentDataStoreBindings): Map[String, Any] = {
    val rs = Cassandra.session.execute(binding.bind())
    var index = 1
    val r = rs.one()
    o.fetchOutputs.map(f => {
      tupleFromRow(f, r)
    }).toMap
  }
}

class CassandraReader extends PersistentDataStoreReader {
  val fetch: String = "Fetch"

  def read(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Map[String, Any] = {
    val stmt = Cassandra.prepareFetch(fetch, o)
    val binding = Cassandra.bindInputs(stmt, o, criteria)
    Cassandra.bindSingleRowOutputs(binding, o)
  }
}

trait CassandraWriter extends PersistentDataStoreWriter {
  val upsert: String = "Upsert"
  def write(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Map[String, Any] = {
    val stmt = Cassandra.prepareUpsert(upsert, o)
    val binding = Cassandra.bindInputs(stmt, o, criteria)
    Cassandra.bindSingleRowOutputs(binding, o)
  }
}
