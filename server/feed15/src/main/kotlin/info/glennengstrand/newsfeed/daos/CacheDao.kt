package info.glennengstrand.newsfeed.daos

import mu.KotlinLogging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.util.concurrent.Callable

interface Cachable<M> {
    fun key(id: Long): String

    fun parse(value: String): M

    fun format(value: M): String

    fun read(id: Long): Mono<M>
}

@Component
class CacheDao {
    private val logger = KotlinLogging.logger {}
    private val cacheHost = System.getenv("CACHE_HOST") ?: "localhost"
    private val cachePort = System.getenv("CACHE_PORT")?.toInt() ?: 6379
    private val cacheTimeout = System.getenv("CACHE_TIMEOUT")?.toInt() ?: 5000
    private val cachePool = System.getenv("CACHE_POOL")?.toInt() ?: 10
    private val pool: JedisPool by lazy {
        val config = JedisPoolConfig()
        config.setMaxTotal(cachePool)
        config.setBlockWhenExhausted(false)
        JedisPool(config, cacheHost, cachePort, cacheTimeout)
    }

    fun <M> get(
        id: Long,
        c: Cachable<M>,
    ): Mono<M> {
        fun processDBRead(value: M): M {
            pool.getResource().use {
                it.set(c.key(id), c.format(value))
            }
            return value
        }

        fun processCacheGet(value: String): Mono<M> {
            return if (value == "") {
                c.read(id).map(::processDBRead)
            } else {
                Callable { c.parse(value) }.toMono()
            }
        }
        return Mono.fromCallable {
            pool.getResource().use {
                try {
                    val v = it.get(c.key(id))
                    when (v) {
                        is String -> v
                        else -> ""
                    }
                } catch (e: Exception) {
                    logger.error(e.getLocalizedMessage())
                    ""
                }
            }
        }.flatMap(::processCacheGet)
    }
}
