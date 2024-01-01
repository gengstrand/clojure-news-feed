package info.glennengstrand.newsfeed.daos

import info.glennengstrand.newsfeed.models.FriendModel
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
interface FriendRepository : ReactiveCrudRepository<FriendModel, Long>, CustomFriendRepository

interface CustomFriendRepository {
    fun findById(id: Long): Flux<FriendModel>

    fun addFriend(
        from: Long,
        to: Long,
    ): Flux<FriendModel>
}

const val FRIEND_SELECT = "call FetchFriends(:id)"
const val FRIEND_UPSERT = "call UpsertFriends(:from, :to)"

class SelectFriendMapper(private val id: Long) : BiFunction<Row, Any, FriendModel> {
    override fun apply(
        row: Row,
        o: Any,
    ): FriendModel {
        return FriendModel(
            row.get("FriendsId", Number::class.java)?.toLong() ?: 0L,
            ParticipantModel(id, "").link,
            ParticipantModel(row.get("ParticipantID", Number::class.java)?.toLong() ?: 0L, "").link,
        )
    }
}

class UpsertFriendMapper(private val from: Long, val to: Long) : BiFunction<Row, Any, FriendModel> {
    override fun apply(
        row: Row,
        o: Any,
    ): FriendModel {
        return FriendModel(
            row.get("id", Number::class.java)?.toLong() ?: 0L,
            ParticipantModel(from, "").link,
            ParticipantModel(to, "").link,
        )
    }
}

@Primary
class CustomFriendRepositoryImpl(
    private val db: DatabaseClient,
) : CustomFriendRepository {
    override fun findById(id: Long): Flux<FriendModel> {
        val mapper = SelectFriendMapper(id)
        return db.sql(FRIEND_SELECT)
            .bind("id", id)
            .map(mapper::apply)
            .all()
    }

    override fun addFriend(
        from: Long,
        to: Long,
    ): Flux<FriendModel> {
        val mapper = UpsertFriendMapper(from, to)
        return db.sql(FRIEND_UPSERT)
            .bind("from", from)
            .bind("to", to)
            .map(mapper::apply)
            .all()
    }
}

@Component
class FriendDao(
    private val repo: CustomFriendRepositoryImpl,
) {
    fun getFriends(id: Long): Mono<List<FriendModel>> {
        return repo.findById(id).collectList()
    }

    fun addFriend(
        id: Long,
        f: FriendModel,
    ): Mono<FriendModel> {
        val fid = ParticipantModel.unlink(f.from)
        val tid = ParticipantModel.unlink(f.to)
        return repo.addFriend(fid, tid).next()
    }
}
