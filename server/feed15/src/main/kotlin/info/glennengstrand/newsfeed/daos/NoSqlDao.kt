package info.glennengstrand.newsfeed.daos

import com.datastax.oss.driver.api.core.CqlSessionBuilder
import java.net.InetSocketAddress
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

open class NoSqlDao {
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

    public fun format(v: Instant?): String = v?.let { f.format(it) } ?: f.format(Instant.now())
}
