package info.glennengstrand.newsfeed.services

import info.glennengstrand.newsfeed.daos.FriendDao
import info.glennengstrand.newsfeed.daos.InboundDao
import info.glennengstrand.newsfeed.daos.OutboundDao
import info.glennengstrand.newsfeed.daos.ParticipantDao
import info.glennengstrand.newsfeed.models.FriendModel
import info.glennengstrand.newsfeed.models.InboundModel
import info.glennengstrand.newsfeed.models.OutboundModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class ParticipantService(
    val participantDao: ParticipantDao,
    val friendDao: FriendDao,
    val inboundDao: InboundDao,
    val outboundDao: OutboundDao,
) {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getParticipant(id: Long): ParticipantModel {
        return participantDao.getParticipant(id)
    }

    fun addParticipant(
        id: Long,
        p: ParticipantModel,
    ): ParticipantModel {
        return participantDao.addParticipant(id, p)
    }

    fun getFriends(id: Long): List<FriendModel> {
        return friendDao.getFriends(id)
    }

    fun addFriend(
        id: Long,
        f: FriendModel,
    ): FriendModel {
        return friendDao.addFriend(id, f)
    }

    fun getInbound(id: Long): List<InboundModel> {
        return inboundDao.getInbound(id)
    }

    fun getOutbound(id: Long): List<OutboundModel> {
        return outboundDao.getOutbound(id)
    }

    fun addOutbound(
        id: Long,
        ob: OutboundModel,
    ): OutboundModel {
        val fp = ParticipantModel(id, "")
        val n = LocalDate.now().format(fmt)
        friendDao.getFriends(id).forEach {
            val tid = ParticipantModel.unlink(it.to)
            val tp = ParticipantModel(tid, "")
            val ib = InboundModel(tp.link, fp.link, n, ob.subject, ob.story)
            inboundDao.addInbound(id, ib)
        }
        return outboundDao.addOutbound(id, ob)
    }
}
