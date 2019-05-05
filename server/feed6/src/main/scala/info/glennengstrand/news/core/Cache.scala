package info.glennengstrand.news.core

import redis.clients.jedis.{ Jedis, JedisPool, JedisPoolConfig }

trait Cache {
  def get(key: String): Option[String]
  def set(key: String, value: String): Unit
  def delete(key: String): Unit
}

object RedisCache {
  def apply(host: String, port: Int, timeout: Int, pool: Int) = {
    val poolConfig = new JedisPoolConfig()
    poolConfig.setMaxTotal(pool)
    poolConfig.setBlockWhenExhausted(false)
    new RedisCache(new JedisPool(poolConfig, host, port, timeout))
  }
}

class RedisCache(pool: JedisPool) extends Cache {
  def get(key: String): Option[String] = {
    val p = pool.getResource()
    val retVal = p.get(key)
    p.close()
    retVal match {
      case (v: String) => Some(retVal)
      case _ => None
    }
  }
  def set(key: String, value: String): Unit = {
    val p = pool.getResource()
    p.set(key, value)
    p.close()
  }
  def delete(key: String): Unit = {
    val p = pool.getResource()
    p.del(key)
    p.close()    
  }

}
class MockCache extends Cache {
  val db = scala.collection.mutable.HashMap[String, String]()
  def get(key: String): Option[String] = {
    db.get(key)
  }
  def set(key: String, value: String): Unit = {
    db.put(key, value)
  }
  def delete(key: String): Unit = {
    db.remove(key)
  }
}
