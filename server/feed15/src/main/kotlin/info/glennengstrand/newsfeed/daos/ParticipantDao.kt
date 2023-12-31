package info.glennengstrand.newsfeed.daos

import info.glennengstrand.newsfeed.models.ParticipantModel
import io.r2dbc.spi.Row
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.BiFunction

@Repository
interface ParticipantRepository : ReactiveCrudRepository<ParticipantModel, Long>, CustomParticipantRepository

interface CustomParticipantRepository {
    fun findById(id: Long): Flux<ParticipantModel>

    fun addParticipant(moniker: String): Flux<ParticipantModel>
}

const val PARTICIPANT_SELECT = "call FetchParticipant(:id)"
const val PARTICIPANT_UPSERT = "call UpsertParticipant(:moniker)"

class SelectParticipantMapper(private val id: Long) : BiFunction<Row, Any, ParticipantModel> {
    override fun apply(
        row: Row,
        o: Any,
    ): ParticipantModel {
        return ParticipantModel(
            id,
            row.get("moniker", String::class.java) ?: "",
        )
    }
}

class UpsertParticipantMapper(private val moniker: String) : BiFunction<Row, Any, ParticipantModel> {
    override fun apply(
        row: Row,
        o: Any,
    ): ParticipantModel {
        return ParticipantModel(
            row.get("id", Number::class.java)?.toLong() ?: 0L,
            moniker,
        )
    }
}

@Primary
class CustomParticipantRepositoryImpl(
    private val db: DatabaseClient,
) : CustomParticipantRepository {
    override fun findById(id: Long): Flux<ParticipantModel> {
        val mapper = SelectParticipantMapper(id)
        return db.sql(PARTICIPANT_SELECT)
            .bind("id", id)
            .map(mapper::apply)
            .all()
    }

    override fun addParticipant(moniker: String): Flux<ParticipantModel> {
        val mapper = UpsertParticipantMapper(moniker)
        return db.sql(PARTICIPANT_UPSERT)
            .bind("moniker", moniker)
            .map(mapper::apply)
            .all()
    }
}

@Component
class ParticipantDao(
    private val repo: CustomParticipantRepositoryImpl,
) {
    fun getParticipant(id: Long): Mono<ParticipantModel> {
        return repo.findById(id).next()
    }

    fun addParticipant(p: ParticipantModel): Mono<ParticipantModel> {
        return repo.addParticipant(p.name).next()
    }
}
