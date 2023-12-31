package info.glennengstrand.newsfeed.daos

import info.glennengstrand.newsfeed.models.FriendModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class FriendDao {
    fun getFriends(id: Long): Mono<List<FriendModel>> {
        return Mono.just(
            listOf(
                FriendModel(
                    1L,
                    ParticipantModel(id, "test").link,
                    "/participant/2",
                ),
            ),
        )
    }

    fun addFriend(
        id: Long,
        f: FriendModel,
    ): Mono<FriendModel> {
        return Mono.just(f)
    }
}
