package info.glennengstrand.newsfeed.services

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import info.glennengstrand.newsfeed.daos.Cachable
import info.glennengstrand.newsfeed.daos.CacheDao
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
    val cacheDao: CacheDao,
) {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    class ParticipantCachable(val dao: ParticipantDao) : Cachable<ParticipantModel> {
        val gson = Gson()

        override fun parse(value: String): ParticipantModel {
            return gson.fromJson(value, ParticipantModel::class.java)
        }

        override fun format(value: ParticipantModel): String {
            return gson.toJson(value)
        }

        override fun read(id: Long): ParticipantModel? {
            return dao.getParticipant(id)
        }
    }

    class FriendCachable(val dao: FriendDao) : Cachable<List<FriendModel>> {
        val gson = Gson()
        val friendListType = object : TypeToken<List<FriendModel>>() {}.type

        override fun parse(value: String): List<FriendModel> {
            return gson.fromJson(value, friendListType)
        }

        override fun format(value: List<FriendModel>): String {
            return gson.toJson(value)
        }

        override fun read(id: Long): List<FriendModel>? {
            return dao.getFriends(id)
        }
    }

    val pcache = ParticipantCachable(participantDao)
    val fcache = FriendCachable(friendDao)

    fun getParticipant(id: Long): ParticipantModel? {
        return cacheDao.get<ParticipantModel>(id, pcache)
    }

    fun addParticipant(
        id: Long,
        p: ParticipantModel,
    ): ParticipantModel {
        return participantDao.addParticipant(id, p)
    }

    fun getFriends(id: Long): List<FriendModel> {
        return cacheDao.get<List<FriendModel>>(id, fcache) ?: listOf()
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
        val n = LocalDate.now().format(fmt)
        friendDao.getFriends(id).forEach {
            val ib = InboundModel(it.to, ob.from, n, ob.subject, ob.story)
            inboundDao.addInbound(id, ib)
        }
        return outboundDao.addOutbound(id, ob)
    }
}
