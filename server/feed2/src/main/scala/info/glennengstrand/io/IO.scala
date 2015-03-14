package info.glennengstrand.io

import com.mchange.v2.c3p0.ComboPooledDataSource
import java.util.Properties
import scala.util.parsing.json.JSON
import java.sql.{PreparedStatement, Connection}

object IO {
  val settings: Properties = new Properties
  val jdbcDriveName: String = "jdbc_driver"
  val jdbcUrl: String = "jdbc_url"
  val jdbcUser: String = "jdbc_user"
  val jdbcPassword: String = "jdbc_password"
  val jdbcMinPoolSize: String = "jdbc_min_pool_size"
  val jdbcMaxPoolSize: String = "jdbc_max_pool_size"
  val jdbcAcquireIncrement: String = "jdbc_acquire_increment"
  val jdbcMaxStatements: String = "jdbc_max_statements"
  val nosqlHost: String = "nosql_host"
  val nosqlKeyspace: String = "nosql_keyspace"
  val nosqlReadConsistencyLevel: String = "nosql_read_consistency_level"

  def cacheAwareRead(o: PersistentDataStoreBindings, criteria: Map[String, Any], reader: PersistentDataStoreReader, cache: CacheAware): Iterable[Map[String, Any]] = {
    def loadFromDbAndCache: Iterable[Map[String, Any]] = {
      val fromDb = reader.read(o, criteria)
      fromDb.size match {
        case 0 =>
        case _ => cache.store(o, fromDb, criteria)
      }
      fromDb
    }
    val fromCache = cache.load(o, criteria)
    fromCache.size match {
      case 0 => loadFromDbAndCache
      case _ => fromCache
    }
  }
  def toJsonValue(v: Any): String = {
    v match {
      case l: Long => l.toString
      case i: Int => i.toString
      case s: String => "\"" + s + "\""
      case _ => "\"" + v.toString + "\""
    }
  }
  def toJson(state: Map[String, Any]): String = {
    val s = state.map(kv => "\"" + kv._1 + "\":" + toJsonValue(kv._2)).reduce(_ + "," + _)
    "{" + s + "}"
  }
  def toJson(state: Iterable[Map[String, Any]]): String = {
    val s = state.map{ li => {
      "{" +li.map(kv => "\"" + kv._1 + "\":" +  toJsonValue(kv._2)).reduce(_ + "," + _) + "}"
    }}.reduce(_ + "," + _)
    "[" + s + "]"
  }
  def fromJson(json: String): Iterable[Map[String, Option[Any]]] = {
    val retVal = JSON.parseFull(json).getOrElse(List())
    retVal match {
      case l: List[Map[String, Option[Any]]] => l
      case m: Map[String, Option[Any]] => List(m)
      case _ => List()
    }
  }
  def fromFormPost(state: String) : Map[String, Any] = {
    state.split("&").map(kv => kv.split("=")).map(t => (t(0), t(1))).toMap
  }
  def convertToLong(v: Any) : Long = {
    v match {
      case l: Long => l
      case d: Double => d.toLong
      case i: Int => i.toLong
      case s: String => s.toLong
    }
  }
}

abstract class FactoryClass {
  def getObject(name: String, id: Long): Option[Object]
  def getObject(name: String, state: String): Option[Object]
}

class EmptyFactoryClass extends FactoryClass {
  def getObject(name: String, id: Long): Option[Object] = {
    None
  }
  def getObject(name: String, state: String): Option[Object] = {
    None
  }
}

abstract class PersistentDataStoreBindings {
  def entity: String
  def fetchInputs: Iterable[String]
  def fetchOutputs: Iterable[(String, String)]
  def fetchOrder: Map[String, String]
  def upsertInputs: Iterable[String]
  def upsertOutputs: Iterable[(String, String)]
}

trait PersistentDataStoreReader {
  def read(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]]
}

trait PersistentDataStoreWriter {
  def write(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Map[String, Any]
}

trait CacheAware {
  def load(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]]
  def store(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit
  def store(o: PersistentDataStoreBindings, state: Iterable[Map[String, Any]], criteria: Map[String, Any]): Unit
  def append(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit
  def invalidate(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Unit
}

trait PooledRelationalDataStore {

  val vendor: String

  lazy val ds: ComboPooledDataSource = getPooledDataSource

  private def getPooledDataSource: ComboPooledDataSource = {
    val ds = new ComboPooledDataSource
    ds.setDriverClass(IO.settings.getProperty(vendor + "_" + IO.jdbcDriveName))
    ds.setJdbcUrl(IO.settings.getProperty(vendor + "_" + IO.jdbcUrl))
    ds.setUser(IO.settings.getProperty(vendor + "_" + IO.jdbcUser))
    ds.setPassword(IO.settings.getProperty(vendor + "_" + IO.jdbcPassword))
    ds.setMinPoolSize(IO.settings.getProperty(vendor + "_" + IO.jdbcMinPoolSize).toInt)
    ds.setAcquireIncrement(IO.settings.getProperty(vendor + "_" + IO.jdbcMinPoolSize).toInt)
    ds.setMaxPoolSize(IO.settings.getProperty(vendor + "_" + IO.jdbcMaxPoolSize).toInt)
    ds.setMaxStatements(IO.settings.getProperty(vendor + "_" + IO.jdbcMaxStatements).toInt)
    ds
  }
  def getDbConnection: Connection = {
    ds.getConnection
  }
}


