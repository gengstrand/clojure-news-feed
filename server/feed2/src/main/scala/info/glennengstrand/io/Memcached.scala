package info.glennengstrand.io

import scala.util.{Try, Failure, Success}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import net.spy.memcached.MemcachedClient;

object MemcachedService {
  val log = LoggerFactory.getLogger("info.glennengstrand.io.MemcachedService")
  def connect: MemcachedClient = {
    val host = IO.settings.get(IO.cacheHost).asInstanceOf[String]
    val port = IO.convertToInt(IO.settings.get(IO.cachePort).asInstanceOf[String], 11211)
    new MemcachedClient(new InetSocketAddress(host, port));
  }
  lazy val memcached = connect
  lazy val ttl = IO.convertToInt(IO.settings.get(IO.cacheTimeout).asInstanceOf[String], 360)
}

trait MemcachedCacheAware extends CacheAware {
  def key(o: PersistentDataStoreBindings, criteria: Map[String, Any]): String = {
    o.entity + ":" + criteria.map((f) => f._2).reduce(_ + ":" + _)
  }
    
  def load(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Iterable[Map[String, Any]] = {
    val cache = MemcachedService.memcached
    val rc = Try {
      val retVal = cache.get(key(o, criteria))
      if (retVal == null) {
        List()
      } else {
        IO.fromJson(retVal.toString())
      }
    }
    rc match {
      case Success(v) => v
      case Failure(e) => {
        MemcachedService.log.warn(e.getLocalizedMessage())
        List()
      }
    }
  }

  def store(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
    val cache = MemcachedService.memcached
    val rc = Try {
      cache.set(key(o, criteria), MemcachedService.ttl, IO.toJson(state))
    }
    rc match {
      case Success(v) => 
      case Failure(e) => {
        MemcachedService.log.warn(e.getLocalizedMessage())
      }
    }
  }

  def store(o: PersistentDataStoreBindings, state: Iterable[Map[String, Any]], criteria: Map[String, Any]): Unit = {
    val cache = MemcachedService.memcached
    val rc = Try {
      cache.set(key(o, criteria), MemcachedService.ttl, IO.toJson(state))
    }
    rc match {
      case Success(v) => 
      case Failure(e) => {
        MemcachedService.log.warn(e.getLocalizedMessage())
      }
    }
  }

  def append(o: PersistentDataStoreBindings, state: Map[String, Any], criteria: Map[String, Any]): Unit = {
    val cache = MemcachedService.memcached
    val rc = Try {
      cache.append(key(o, criteria), IO.toJson(state))
    }
    rc match {
      case Success(v) => 
      case Failure(e) => {
        MemcachedService.log.warn(e.getLocalizedMessage())
      }
    }
  }

  def invalidate(o: PersistentDataStoreBindings, criteria: Map[String, Any]): Unit = {
    val cache = MemcachedService.memcached
    val rc = Try {
      cache.delete(key(o, criteria))
    }
    rc match {
      case Success(v) => 
      case Failure(e) => {
        MemcachedService.log.warn(e.getLocalizedMessage())
      }
    }
  }  
}

class MemcachedCache extends MemcachedCacheAware {
  
}