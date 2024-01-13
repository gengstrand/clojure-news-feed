package info.glennengstrand.newsfeed.daos

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Component
class SearchDao {
    @Value("es.pool.core")
    private var corePoolSize: Int = 3

    @Value("es.pool.max")
    private var maxPoolSize: Int = 5

    @Value("es.pool.min")
    private var minPoolSize: Int = 2

    private val logger = KotlinLogging.logger {}
    private val gson = Gson()
    private val esRespType = object : TypeToken<Map<String, Any>>() {}.type
    private val esHost = System.getenv("SEARCH_HOST") ?: "localhost"
    private val esClient = WebClient.create("http://$esHost:9200")

    public val pool: ForkJoinPool by lazy {
        ForkJoinPool(
            Runtime.getRuntime().availableProcessors(),
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            null,
            true,
            corePoolSize,
            maxPoolSize,
            minPoolSize,
            null,
            60,
            TimeUnit.SECONDS,
        )
    }

    data class SearchDoc(val sender: Long, val story: String) {
        val id: String
            get() = generateId(sender)

        private fun generateId(sender: Long): String {
            val ns = Instant.now().getEpochSecond()
            val r = Random.nextInt(0, 1000)
            return "$sender-$ns-$r"
        }
    }

    private fun extract(v: Map<String, Any>): Mono<List<Long>> {
        if (v.containsKey("hits")) {
            val h1 = v["hits"] as Map<String, Any>
            if (h1.containsKey("hits")) {
                val h2 = h1["hits"] as List<Map<String, Any>>
                return Mono.just(
                    h2.filter { it.containsKey("_source") }
                        .map { it["_source"] as Map<String, Any> }
                        .filter { it.containsKey("sender") }
                        .map { (it["sender"] as Double).toLong() },
                )
            } else {
                logger.error("inner hits missing from es response")
            }
        } else {
            logger.error("outer hits missing from es response")
        }
        return Mono.just(listOf())
    }

    fun indexStory(
        id: Long,
        story: String,
    ) {
        fun upsert() {
            val d = SearchDoc(id, story)
            esClient.put()
                .uri("/feed/stories/${d.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(d))
                .retrieve()
                .toEntity(String::class.java)
                .block()
        }
        logger.info("indexing $story for participant $id")
        CompletableFuture.runAsync(::upsert, pool)
    }

    fun searchOutbound(keywords: String): Mono<List<Long>> {
        logger.info("searching for $keywords")
        return esClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/feed/_search")
                    .queryParam("q", keywords)
                    .queryParam("size", 1000)
                    .build()
            }
            .retrieve()
            .toEntity(String::class.java)
            .flatMap {
                var rv: Map<String, Any> = mapOf()
                if (it.getStatusCode().is2xxSuccessful()) {
                    rv = gson.fromJson(it.getBody(), esRespType) as Map<String, Any>
                }
                Mono.just(rv)
            }
            .flatMap(::extract)
    }
}
