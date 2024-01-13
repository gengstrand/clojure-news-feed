package info.glennengstrand.newsfeed.daos

import com.datastax.oss.driver.api.core.CqlSessionBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

@Component
class NoSqlDao {
    @Value("\${cql.pool.core}")
    private var corePoolSize: Int = 10

    @Value("\${cql.pool.max}")
    private var maxPoolSize: Int = 15

    @Value("\${cql.pool.min}")
    private var minPoolSize: Int = 5

    private val noSqlHost = System.getenv("NOSQL_HOST") ?: "localhost"
    private val noSqlKeyspace = System.getenv("NOSQL_KEYSPACE") ?: "activity"
    private val cp = InetSocketAddress(noSqlHost, 9042)
    private val f =
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault())

    public val noSqlTtl = System.getenv("NOSQL_TTL")?.toLong() ?: TimeUnit.DAYS.toMillis(1)

    public fun connect(f: (CqlSessionBuilder) -> Unit) {
        return f(
            CqlSessionBuilder()
                .addContactPoint(cp)
                .withLocalDatacenter("datacenter1")
                .withKeyspace(noSqlKeyspace),
        )
    }

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

    public fun format(v: Instant?): String = v?.let { f.format(it) } ?: f.format(Instant.now())
}
