package info.glennengstrand.newsfeed.daos

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import com.datastax.oss.driver.api.core.cql.PreparedStatement
import info.glennengstrand.newsfeed.models.InboundModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import mu.KotlinLogging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

@Component
class InboundDao {
    private val n = NoSqlDao()
    private val logger = KotlinLogging.logger {}
    private val selectCql = """select toTimestamp(occurred) as occurred, fromparticipantid, subject, story 
    from Inbound where participantid = ? order by occurred desc"""
    private val insertCql = """insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) 
    values (?, ?, now(), ?, ?)"""
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

    fun getInbound(id: Long): Mono<List<InboundModel>> {
        val from = ParticipantModel(id, "").link
        return Mono.fromFuture {
            CompletableFuture.supplyAsync<List<InboundModel>> {
                val bs = selectStatement.bind(id.toInt())
                val rs = session.execute(bs)
                val rv = mutableListOf<InboundModel>()
                rs.forEach {
                    rv.add(
                        InboundModel(
                            ParticipantModel(it.getInt(1).toLong(), "").link,
                            from,
                            n.format(it.getInstant(0)),
                            it.getString(2)!!,
                            it.getString(3)!!,
                        ),
                    )
                }
                rv
            }
        }
    }

    fun addInbound(
        id: Long,
        ib: InboundModel,
    ): Mono<InboundModel> {
        val bs =
            insertStatement.bind(
                ParticipantModel.unlink(ib.to).toInt(),
                id.toInt(),
                ib.subject,
                ib.story,
            )
        session.executeAsync(bs)
        return Mono.just(ib)
    }
}
