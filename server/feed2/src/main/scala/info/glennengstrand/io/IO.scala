package info.glennengstrand.io

import java.text.{SimpleDateFormat, DateFormat}
import java.util.logging.{Logger, Level}

import scala.util.Random
import scala.util.{Try, Failure, Success}
import java.util.concurrent.Executors
import scala.compat.Platform
import java.util.{Calendar, Date, Properties}
import scala.util.parsing.json.JSON
import java.sql.{SQLException, PreparedStatement, Connection}
import com.twitter.util.{FuturePool, Future}

/** general common Input Output related helper functions */
object IO {
  val settings = new Properties
  val df = new SimpleDateFormat("yyyy-MM-dd")
  val log = Logger.getLogger("info.glennengstrand.io.IO")
  val r = new Random(Platform.currentTime)
  val jdbcVendor = "jdbc_vendor"
  val jdbcDriveName = "jdbc_driver"
  val jdbcUrl = "jdbc_url"
  val jdbcUser = "jdbc_user"
  val jdbcPassword = "jdbc_password"
  val jdbcMinPoolSize = "jdbc_min_pool_size"
  val jdbcMaxPoolSize = "jdbc_max_pool_size"
  val jdbcAcquireIncrement = "jdbc_acquire_increment"
  val jdbcMaxStatements = "jdbc_max_statements"
  val nosqlHost = "nosql_host"
  val nosqlKeyspace = "nosql_keyspace"
  val nosqlReadConsistencyLevel = "nosql_read_consistency_level"
  val nosqlTimeToLiveInSeconds = "nosql_ttl"
  val messagingBrokers = "messaging_brokers"
  val zookeeperServers = "zookeeper_servers"
  val searchHost = "search_host"
  val cacheHost = "cache_host"
  val cachePort = "cache_port"
  val cacheTimeout = "cache_timeout"

  val sql: scala.collection.mutable.Map[String, Array[PreparedStatement]] = scala.collection.mutable.Map()
  lazy val workerPool = FuturePool.unboundedPool
  
  var cacheStatements = true
  var unitTesting = false

  /** convert string to int */
  def convertToInt(value: String, defaultValue: Int): Int = {
    val retVal = Try {
      Integer.parseInt(value)
    }
    retVal match {
      case Success(rv) => rv
      case Failure(e) => defaultValue
    }
  }
  
  /** convert string to date */
  def convertToDate(value: String): Date = {
    val retVal = Try(df.parse(value))
    retVal match {
      case Success(d) => d
      case Failure(e) => {
        log.warning(s"error ${e.getLocalizedMessage()} when attempting to parse $value as date")
        new Date()
      }
    }
  }
  
  /** check the cache first, if a hit, then return that else check the db and write to the cache */
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

  /** format a value acceptable for JSON based on type */
  def toJsonValue(v: Any): String = {
    v match {
      case l: Long => l.toString
      case i: Int => i.toString
      case s: String => "\"" + s + "\""
      case o: MicroServiceSerializable => o.toJson
      case d: Date => "\"" + df.format(d) + "\""
      case _ => "\"" + v.toString + "\""
    }
  }

  /** serialize a map as JSON */
  def toJson(state: Map[String, Any]): String = {
    val s = state.map(kv => "\"" + kv._1 + "\":" + toJsonValue(kv._2)).reduce(_ + "," + _)
    "{" + s + "}"
  }

  /** serialize a collection as JSON */
  def toJson(state: Iterable[Map[String, Any]]): String = {
    val s = state.map{ li => {
      "{" +li.map(kv => "\"" + kv._1 + "\":" +  toJsonValue(kv._2)).reduce(_ + "," + _) + "}"
    }}.reduce(_ + "," + _)
    "[" + s + "]"
  }

  /** parse JSON into a collection of maps */
  def fromJson(json: String): Iterable[Map[String, Option[Any]]] = {
    val retVal = JSON.parseFull(json).getOrElse(List())
    retVal match {
      case l: List[Map[String, Option[Any]]] => l
      case m: Map[String, Option[Any]] => List(m)
      case _ => List()
    }
  }

  /** convert a form post into a map */
  def fromFormPost(state: String) : Map[String, Any] = {
    state.isEmpty match {
      case false => state.split("&").map(kv => kv.split("=")).map(t => (t(0), t(1))).toMap
      case _ => Map()
    }
  }

  /** convert a value to long based on type */
  def convertToLong(v: Any) : Long = {
    v match {
      case l: Long => l
      case d: Double => d.toLong
      case i: Int => i.toLong
      case s: String => s.toLong
    }
  }

  /** convert a value to int based on type */
  def convertToInt(v: Any) : Int = {
    v match {
      case l: Long => l.toInt
      case d: Double => d.toInt
      case i: Int => i
      case s: String => s.toInt
    }
  }

  /** convert a value to date based on type */
  def convertToDate(v: Any): Date = {
    v match {
      case l: Long => new Date(l)
      case d: Date => d
      case s: String => df.parse(s)
    }
  }

  def getReader: PersistentDataStoreReader = {
    IO.settings.getProperty(IO.jdbcVendor) match {
      case "mysql" => new MySqlReader
      case _ => new PostgreSqlReader
    }
  }

}

/** responsible for entity object creation for both unit tests and the real service */
abstract class FactoryClass {
  def getObject(name: String, id: Int): Option[Object]
  def getObject(name: String, state: String): Option[Object]
  def getObject(name: String): Option[Object]
  def isEmpty: Boolean
}

/** default do nothing class factory */
class EmptyFactoryClass extends FactoryClass {
  def isEmpty: Boolean = true
  def getObject(name: String, id: Int): Option[Object] = {
    None
  }
  def getObject(name: String, state: String): Option[Object] = {
    None
  }
  def getObject(name: String): Option[Object] = {
    None
  }
}

/** how each entity communicates with DB aware traits and classes */
abstract class PersistentDataStoreBindings {
  /** identifies which entity in the database that this is for */
  def entity: String

  /** specifies key names to the criteria map whose values are to be used as inputs to the fetch statement */
  def fetchInputs: Iterable[String]

  /** the names of the expected columns from output of the fetch statement and their associated types */
  def fetchOutputs: Iterable[(String, String)]

  /** used to statement generation for ordering. the first string is the column name and the second streing is either asc or desc */
  def fetchOrder: Map[String, String]

  /** specifies key names to the state map whose values are to be used as inputs to the upsert statement */
  def upsertInputs: Iterable[String]

  /** specifies names of the expected columns from the output of the upsert statement and their associated types */
  def upsertOutputs: Iterable[(String, String)]

  /** look up the field name and return the string representation of its type */
  def getTypeOf(fieldName: String): String = {
    val retVal = for ((fn, ft) <- fetchOutputs if fieldName == fn) yield ft
    retVal.isEmpty match {
      case true => "Int"
      case _ => retVal.head
    }
  }
}

/** responsible for managing the cache of prepared statements */
trait PersistentRelationalDataStoreStatementAware {

  /** generate the string representation of the  SQL to be used to create the prepared statement */
  def generatePreparedStatement(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)]): String

  /** clear out the cache of prepared statements */
  def reset: Unit = {
    IO.sql.synchronized {
      IO.sql.clear()
    }
  }

  /** retrieve a prepared statement from the cache or create the statement and add it to the cache if necessary */
  def prepare(operation: String, entity: String, inputs: Iterable[String], outputs: Iterable[(String, String)], pool: PooledRelationalDataStore): PreparedStatement = {
    val max = IO.settings.getProperty(IO.jdbcMinPoolSize, "1").toInt
    def prepareStatementPool(sql: String): Array[PreparedStatement] = {
      IO.unitTesting match {
        case true => 1.to(max).map(x => new MockPreparedStatement).toArray
        case _ => 1.to(max).map(x => pool.getDbConnection.prepareStatement(sql)).toArray
      }
    }
    if (IO.cacheStatements) {
      val key = operation + ":" + entity
      IO.sql.contains(key) match {
        case false => {
          IO.sql.synchronized {
            IO.sql.contains(key) match {
              case false => {
                val stmt = prepareStatementPool(generatePreparedStatement(operation, entity, inputs, outputs))
                IO.sql.put(key, stmt)
                stmt(IO.r.nextInt(max))
              }
              case true => IO.sql.get(key).get.apply(IO.r.nextInt(max))
            }
          }
        }
        case true => IO.sql.get(key).get.apply(IO.r.nextInt(max))
      }
    } else {
      IO.unitTesting match {
        case true => new MockPreparedStatement
        case _ => pool.getDbConnection.prepareStatement(generatePreparedStatement(operation, entity, inputs, outputs))
      }
    }
  }
}

/** specifies the contract for data store readers */
trait PersistentDataStoreReader {

  /** read from the data store based on bindings and criteria */
  def read(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]]
}

/** specifies the contract for JDBC readers */
trait PersistentRelationalDataStoreReader extends PersistentDataStoreReader with PooledRelationalDataStore with PersistentRelationalDataStoreStatementAware {
  val operation = "Fetch"

  /** read from the SQL database based on bindings and criteria */
  def read(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]] = {
    val stmt = prepare(operation, o.entity, o.fetchInputs, o.fetchOutputs, this)
    try {
      stmt.synchronized {
        Sql.prepare(stmt, o.fetchInputs, criteria)
        Sql.query(stmt, o.fetchOutputs)
      }
    } catch {
      case e: SQLException => {
        IO.log.log(Level.WARNING, "cannot fetch data\n", e)
        reset
        val stmt = prepare(operation, o.entity, o.fetchInputs, o.fetchOutputs, this)
        stmt.synchronized {
          Sql.prepare(stmt, o.fetchInputs, criteria)
          Sql.query(stmt, o.fetchOutputs)
        }
      }
    }
  }
}

/** specifies the contract for data store writers */
trait PersistentDataStoreWriter {

  /** writes to the data store based on bindings, state, and criteria */
  def write(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Map[String, Any]
}

/** specifies the contract for JDBC writers */
trait PersistentRelationalDataStoreWriter extends PersistentDataStoreWriter with PooledRelationalDataStore with PersistentRelationalDataStoreStatementAware {
  val operation = "Upsert"

  /** write to the SQL database based on bindings, state, and criteria returnings the newly created primary key */
  def write(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Map[String, Any] = {
    val stmt = prepare(operation, o.entity, o.upsertInputs, o.upsertOutputs, this)
    try {
      stmt.synchronized {
        Sql.prepare(stmt, o.upsertInputs, state)
        Sql.execute(stmt, o.upsertOutputs).toMap[String, Any]
      }
    } catch {
      case e: SQLException => {
        IO.log.log(Level.WARNING, "cannot upsert data\n", e)
        reset
        val stmt = prepare(operation, o.entity, o.upsertInputs, o.upsertOutputs, this)
        stmt.synchronized {
          Sql.prepare(stmt, o.upsertInputs, state)
          Sql.execute(stmt, o.upsertOutputs).toMap[String, Any]
        }
      }
    }
  }
}

/** specifies the contract for keyword based search */
trait PersistentDataStoreSearcher {

  /** search the collection of documents for keywords */
  def search(terms: String): Iterable[java.lang.Long]

  /** add a new document to the collection */
  def index(id: Long, content: String): Unit
}

/** specifies the contract for logging performance related data */
trait PerformanceLogger {

  /** format the record of performance data to be logged */
  def logRecord(entity: String, operation: String, duration: Long): String = {
    val now = Calendar.getInstance()
    val ts = now.get(Calendar.YEAR).toString + "|" + now.get(Calendar.MONTH).toString + "|" + now.get(Calendar.DAY_OF_MONTH).toString + "|" + now.get(Calendar.HOUR_OF_DAY).toString + "|" + now.get(Calendar.MINUTE).toString
    ts + "|" + entity + "|" + operation + "|" + duration.toString
  }

  /** performs the log operation */
  def log(topic: String, entity: String, operation: String, duration: Long): Unit
}

/** specifies the contract for a write through cache */
trait CacheAware {

  /** fetch from the cache */
  def load(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]]

  /** store a single item to the cache */
  def store(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit

  /** store a collection of items to the cache */
  def store(o: PersistentDataStoreBindings, state: Iterable[Map[String, Any]], criteria: Map[String, Any]): Unit

  /** append an item to a collection of items in the cache */
  def append(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit

  /** remove an item from the cache */
  def invalidate(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Unit
}

/** a do nothing cache for data stores that don't need caching */
trait MockCacheAware extends CacheAware {
  def load(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]] = {
    List()
  }
  def store(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
  }
  def store(o: PersistentDataStoreBindings, state: Iterable[Map[String, Any]], criteria: Map[String, Any]): Unit = {
  }
  def append(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
  }
  def invalidate(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Unit = {
  }
}

class MockCache extends MockCacheAware

/** specifies the contract for serializing entity object state */
trait MicroServiceSerializable {

  /** serialize state to JSON for a single object */
  def toJson: String

  /** serialize state to JSON for a parent entity object that has child entity objects */
  def toJson(factory: FactoryClass): String
}
