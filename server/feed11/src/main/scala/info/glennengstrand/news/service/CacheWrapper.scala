package info.glennengstrand.news.service

import net.sf.ehcache.{CacheManager, Cache, Element}
import net.sf.ehcache.config.{CacheConfiguration, PersistenceConfiguration}
import net.sf.ehcache.store.MemoryStoreEvictionPolicy

object CacheWrapper {
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
  def get(key: String): Option[Object] = {
    val e = cache.get(key)
    if (e == null) {
      None
    } else {
      Option(e.getObjectValue)
    }
  }
  def put(key: String, value: Object): Unit = {
    cache.put(new Element(key, value))
  }
  def del(key: String): Unit = {
    cache.remove(key)
  }
  def shutdown: Unit = {
    cm.shutdown()
  }
}