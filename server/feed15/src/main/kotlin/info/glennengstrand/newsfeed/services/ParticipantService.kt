package info.glennengstrand.newsfeed.services

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import info.glennengstrand.newsfeed.daos.Cachable
import info.glennengstrand.newsfeed.daos.CacheDao
import info.glennengstrand.newsfeed.daos.FriendDao
import info.glennengstrand.newsfeed.daos.InboundDao
import info.glennengstrand.newsfeed.daos.OutboundDao
import info.glennengstrand.newsfeed.daos.ParticipantDao
import info.glennengstrand.newsfeed.daos.SearchDao
import info.glennengstrand.newsfeed.models.FriendModel
import info.glennengstrand.newsfeed.models.InboundModel
import info.glennengstrand.newsfeed.models.OutboundModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class ParticipantService(
    val participantDao: ParticipantDao,
    val friendDao: FriendDao,
    val inboundDao: InboundDao,
    val outboundDao: OutboundDao,
    val cacheDao: CacheDao,
    val searchDao: SearchDao,
) {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    class ParticipantCachable(val dao: ParticipantDao) : Cachable<ParticipantModel> {
        val gson = Gson()

        override fun parse(value: Mono<String>): Mono<ParticipantModel> {
            return value.map { gson.fromJson(it, ParticipantModel::class.java) }
        }

        override fun format(value: Mono<ParticipantModel>): Mono<String> {
            return value.map { gson.toJson(it) }
        }

        override fun read(id: Long): Mono<ParticipantModel> {
            return dao.getParticipant(id)
        }
    }

    class FriendCachable(val dao: FriendDao) : Cachable<List<FriendModel>> {
        val gson = Gson()
        val friendListType = object : TypeToken<List<FriendModel>>() {}.type

        override fun parse(value: Mono<String>): Mono<List<FriendModel>> {
            return value.map { gson.fromJson(it, friendListType) }
        }

        override fun format(value: Mono<List<FriendModel>>): Mono<String> {
            return value.map { gson.toJson(it) }
        }

        override fun read(id: Long): Mono<List<FriendModel>> {
            return dao.getFriends(id)
        }
    }

    val pcache = ParticipantCachable(participantDao)
    val fcache = FriendCachable(friendDao)

    fun getParticipant(id: Long): Mono<ParticipantModel> {
        return cacheDao.get<ParticipantModel>(id, pcache)
    }

    fun addParticipant(p: ParticipantModel): Mono<ParticipantModel> {
        return participantDao.addParticipant(p)
    }

    fun getFriends(id: Long): Mono<List<FriendModel>> {
        return cacheDao.get<List<FriendModel>>(id, fcache)
    }

    fun addFriend(
        id: Long,
        f: FriendModel,
    ): Mono<FriendModel> {
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
        friendDao.getFriends(id).subscribe {
            it.forEach {
                val ib = InboundModel(it.to, ob.from, n, ob.subject, ob.story)
                inboundDao.addInbound(id, ib)
            }
        }
        searchDao.indexStory(id, ob.story)
        return outboundDao.addOutbound(id, ob)
    }
}
