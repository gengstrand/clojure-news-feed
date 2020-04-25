package info.glennengstrand.news.service

import net.sf.ehcache.{CacheManager, Cache, Element}
import net.sf.ehcache.config.{CacheConfiguration, PersistenceConfiguration}
import net.sf.ehcache.store.MemoryStoreEvictionPolicy
import redis.clients.jedis.{ Jedis, JedisPool, JedisPoolConfig }

trait CacheWrapper {
  def get(key: String): Option[Object]
  def put(key: String, value: Object): Unit
  def del(key: String): Unit
  def close: Unit
}

object RedisCache extends CacheWrapper {
  private def connect: JedisPool = {
    val cacheHost = sys.env.get("CACHE_HOST").getOrElse("localhost")
    val cachePort = sys.env.get("CACHE_PORT").map(_.toInt).getOrElse(6379)
    val cacheTimeout = sys.env.get("CACHE_TIMEOUT").map(_.toInt).getOrElse(5000)
    val cachePool = sys.env.get("CACHE_POOL").map(_.toInt).getOrElse(10)
    val poolConfig = new JedisPoolConfig()
    poolConfig.setMaxTotal(cachePool)
    poolConfig.setBlockWhenExhausted(false)
    new JedisPool(poolConfig, cacheHost, cachePort, cacheTimeout)
  }
  private lazy val pool = connect
  
  override def get(key: String): Option[Object] = {
    val p = pool.getResource()
    val retVal = p.get(key)
    p.close()
    retVal match {
      case (v: String) => Some(retVal)
      case _ => None
    }
  }
  override def put(key: String, value: Object): Unit = {
    val p = pool.getResource()
    p.set(key, value.toString())
    p.close()
  }
  override def del(key: String): Unit = {
    val p = pool.getResource()
    p.del(key)
    p.close()
  }
  override def close: Unit = {
    pool.close()
  }
}

object InMemoryCache extends CacheWrapper {
  private lazy val cm = CacheManager.create
  private def createCacheConfig: CacheConfiguration = {
    new CacheConfiguration("local-in-memory-only", 5000)
    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
    .eternal(false)
    .timeToLiveSeconds(120)
    .timeToIdleSeconds(240)
    .diskExpiryThreadIntervalSeconds(40)
    .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE.toString()))
  }
  private def createCache: Cache = {
    val rv = new Cache(createCacheConfig)
    cm.addCache(rv)
    rv
  }
  private lazy val cache = createCache
  override def get(key: String): Option[Object] = {
    val e = cache.get(key)
    if (e == null) {
      None
    } else {
      Option(e.getObjectValue)
    }
  }
  override def put(key: String, value: Object): Unit = {
    cache.put(new Element(key, value))
  }
  override def del(key: String): Unit = {
    cache.remove(key)
  }
  override def close: Unit = {
    cm.shutdown()
  }
}