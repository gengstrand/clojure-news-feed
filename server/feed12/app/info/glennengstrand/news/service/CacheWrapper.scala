package info.glennengstrand.news.service

import redis.clients.jedis.{ Jedis, JedisPool, JedisPoolConfig }
import scala.collection.mutable.Map

trait CacheWrapper {
  def get(key: String): Option[Object]
  def put(key: String, value: Object): Unit
  def del(key: String): Unit
  def close: Unit
}

class RedisCache extends CacheWrapper {
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

class MockCache extends CacheWrapper {
  private val cache: Map[String, Object] = Map()
  def get(key: String): Option[Object] = {
    cache.contains(key) match {
      case true => Some(cache.get(key))
      case _ => None
    }
  }
  def put(key: String, value: Object): Unit = {
    cache += ((key, value))
  }
  def del(key: String): Unit = {
  }
  def close: Unit = {
  }
}