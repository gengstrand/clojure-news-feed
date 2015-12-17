package info.glennengstrand.io

import scredis._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.{Try, Failure, Success}
import java.util.logging.Logger
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/** helper function for connecting to redis */
object RedisService {
  val log = Logger.getLogger("info.glennengstrand.io.RedisService")
  def connect: Redis = {
    new Redis(IO.settings.get(IO.cacheConfig).asInstanceOf[String], "scredis")
  }
  def connectJedis: JedisPool = {
    new JedisPool(new JedisPoolConfig(), "localhost")
  }
  lazy val redis = connect
  lazy val jedis = connectJedis
  val timeout: Long = 60000
}

trait JedisCacheAware extends CacheAware {
  
  def key(o: PersistentDataStoreBindings, criteria: Map[String, Any]): String = {
    o.entity + ":" + criteria.map((f) => f._2).reduce(_ + ":" + _)
  }
    
  def load(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]] = {
    val cache = RedisService.jedis.getResource()
    val rc = Try {
      val retVal = cache.get(key(o, criteria))
      if (retVal == null) {
        List()
      } else {
        IO.fromJson(retVal)
      }
    }
    cache.close()
    rc match {
      case Success(v) => v
      case Failure(e) => {
        RedisService.log.warning(e.getLocalizedMessage())
        List()
      }
    }
  }

  def store(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
    val cache = RedisService.jedis.getResource()
    val rc = Try {
      cache.set(key(o, criteria), IO.toJson(state))
    }
    cache.close()
    rc match {
      case Success(v) => 
      case Failure(e) => {
        RedisService.log.warning(e.getLocalizedMessage())
      }
    }
  }

  def store(o: PersistentDataStoreBindings, state: Iterable[Map[String, Any]], criteria: Map[String, Any]): Unit = {
    val cache = RedisService.jedis.getResource()
    val rc = Try {
      cache.set(key(o, criteria), IO.toJson(state))
    }
    cache.close()
    rc match {
      case Success(v) => 
      case Failure(e) => {
        RedisService.log.warning(e.getLocalizedMessage())
      }
    }
  }

  def append(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
    val cache = RedisService.jedis.getResource()
    val rc = Try {
      cache.append(key(o, criteria), IO.toJson(state))
    }
    cache.close()
    rc match {
      case Success(v) => 
      case Failure(e) => {
        RedisService.log.warning(e.getLocalizedMessage())
      }
    }
  }

  def invalidate(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Unit = {
    val cache = RedisService.jedis.getResource()
    val rc = Try {
      cache.del(key(o, criteria))
    }
    cache.close()
    rc match {
      case Success(v) => 
      case Failure(e) => {
        RedisService.log.warning(e.getLocalizedMessage())
      }
    }
  }
}

/** knows how to cache data in redis */
trait RedisCacheAware extends CacheAware {
  def key(o: PersistentDataStoreBindings, criteria: Map[String, Any]): String = {
    o.entity + ":" + criteria.map((f) => f._2).reduce(_ + ":" + _)
  }

  def load(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]] = {
    val fr: Future[Option[String]] = RedisService.redis.get[String](key(o, criteria))
    val frc: Try[Option[String]] = Await.ready(fr, Duration.Inf).value.get
    frc match {
      case Success(Some(value)) => {
        IO.fromJson(value.asInstanceOf[String])
      }
      case Success(None) => List()
      case Failure(e) => List()
    }
  }

  def store(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
    RedisService.redis.set(key(o, criteria), IO.toJson(state))
  }

  def store(o: PersistentDataStoreBindings, state: Iterable[Map[String, Any]], criteria: Map[String, Any]): Unit = {
    RedisService.redis.set(key(o, criteria), IO.toJson(state))
  }

  def append(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
    RedisService.redis.append(key(o, criteria), IO.toJson(state))
  }

  def invalidate(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Unit = {
    RedisService.redis.del(key(o, criteria))
  }
}

/** used for the read side */
class RedisCache extends JedisCacheAware {

}
