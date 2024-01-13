package info.glennengstrand.newsfeed.daos

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import com.datastax.oss.driver.api.core.cql.PreparedStatement
import info.glennengstrand.newsfeed.models.OutboundModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import mu.KotlinLogging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

@Component
class OutboundDao(
    private val n: NoSqlDao,
) {
    private val logger = KotlinLogging.logger {}
    private val selectCql = """select toTimestamp(occurred) as occurred, subject, story 
    from Outbound where participantid = ? order by occurred desc"""
    private val insertCql = """insert into Outbound (ParticipantID, Occurred, Subject, Story) 
    values (?, now(), ?, ?)"""
    private val selectStatement: PreparedStatement by lazy {
        session.prepare(selectCql)
    }
    private val insertStatement: PreparedStatement by lazy {
        session.prepare(insertCql)
    }
    private val session: CqlSession by lazy {
        var rv: CqlSessionBuilder? = null
        n.connect {
            rv = it
        }
        rv?.build()!!
    }

    fun getOutbound(id: Long): Mono<List<OutboundModel>> {
        val from = ParticipantModel(id, "").link

        fun fetch(): List<OutboundModel> {
            val bs = selectStatement.bind(id.toInt())
            val rs = session.execute(bs)
            val rv = mutableListOf<OutboundModel>()
            rs.forEach {
                rv.add(
                    OutboundModel(
                        from,
                        n.format(it.getInstant(0)),
                        it.getString(1)!!,
                        it.getString(2)!!,
                    ),
                )
            }
            return rv
        }
        return Mono.fromFuture {
            CompletableFuture.supplyAsync<List<OutboundModel>>(::fetch, n.pool)
        }
    }

    fun addOutbound(
        id: Long,
        ob: OutboundModel,
    ): Mono<OutboundModel> {
        val bs =
            insertStatement.bind(
                id.toInt(),
                ob.subject,
                ob.story,
            )
        session.executeAsync(bs)
        return Mono.just(ob)
    }
}
