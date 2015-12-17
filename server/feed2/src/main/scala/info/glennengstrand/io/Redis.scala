package info.glennengstrand.io

import scala.util.{Try, Failure, Success}
import java.util.logging.Logger
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/** helper function for connecting to redis */
object RedisService {
  val log = Logger.getLogger("info.glennengstrand.io.RedisService")

  def connectJedis: JedisPool = {
    new JedisPool(new JedisPoolConfig(), IO.settings.get(IO.cacheHost).asInstanceOf[String])
  }
  lazy val jedis = connectJedis
}

/** knows how to cache data in redis */
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

/** used for the read side */
class RedisCache extends JedisCacheAware {

}
