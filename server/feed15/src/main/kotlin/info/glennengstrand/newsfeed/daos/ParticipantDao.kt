package info.glennengstrand.newsfeed.daos

import info.glennengstrand.newsfeed.models.ParticipantModel
import org.springframework.stereotype.Component

@Component
class ParticipantDao {
    fun getParticipant(id: Long): ParticipantModel? {
        return ParticipantModel(id, "test")
    }

    fun addParticipant(p: ParticipantModel): ParticipantModel {
        return p
    }
}
