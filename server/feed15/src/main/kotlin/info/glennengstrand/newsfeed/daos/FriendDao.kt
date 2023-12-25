package info.glennengstrand.newsfeed.daos

import info.glennengstrand.newsfeed.models.FriendModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import org.springframework.stereotype.Component

@Component
class FriendDao {
    fun getFriends(id: Long): List<FriendModel> {
        return listOf(
            FriendModel(
                1L,
                ParticipantModel(id, "test").link,
                "/participant/2",
            ),
        )
    }

    fun addFriend(
        id: Long,
        f: FriendModel,
    ): FriendModel {
        return f
    }
}
