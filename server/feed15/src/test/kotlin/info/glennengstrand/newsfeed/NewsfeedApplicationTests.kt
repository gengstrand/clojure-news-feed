package info.glennengstrand.newsfeed

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
import info.glennengstrand.newsfeed.services.OutboundService
import info.glennengstrand.newsfeed.services.ParticipantService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

class NewsfeedApplicationTests {
    private val participantDao = mockk<ParticipantDao>(relaxed = true)
    private val friendDao = mockk<FriendDao>(relaxed = true)
    private val inboundDao = mockk<InboundDao>(relaxed = true)
    private val outboundDao = mockk<OutboundDao>(relaxed = true)
    private val cacheDao = mockk<CacheDao>(relaxed = true)
    private val searchDao = mockk<SearchDao>(relaxed = true)
    private val participantService =
        ParticipantService(
            participantDao,
            friendDao,
            inboundDao,
            outboundDao,
            cacheDao,
            searchDao,
        )
    private val outboundService = OutboundService(searchDao)
    private val pid = 1L
    private val tp = ParticipantModel(pid, "test")
    private val tf = FriendModel(pid, tp.link, "/participant/2")
    private val tfl = listOf(tf)
    private val tib = listOf(InboundModel("/participant/2", tp.link, "2023-12-24", "test subject", "test story"))
    private val tob = OutboundModel(tp.link, "2023-12-24", "test subject", "test story")
    private val tobl = listOf(tob)

    @Test
    fun getParticipant() {
        coEvery {
            participantDao.getParticipant(pid)
        } returns Mono.just(tp)
        coEvery {
            cacheDao.get<ParticipantModel>(pid, any())
        } returns participantDao.getParticipant(pid)
        val t = participantService.getParticipant(pid)
        t.subscribe { Assertions.assertEquals(it.name, tp.name) }
        t.block()
    }

    @Test
    fun addParticipant() {
        coEvery {
            participantDao.addParticipant(tp)
        } returns Mono.just(tp)
        val t = participantService.addParticipant(tp)
        t.subscribe { Assertions.assertEquals(it.name, tp.name) }
        t.block()
    }

    @Test
    fun getFriends() {
        coEvery {
            friendDao.getFriends(pid)
        } returns Mono.just(tfl)
        coEvery {
            cacheDao.get<List<FriendModel>>(pid, any())
        } returns friendDao.getFriends(pid)
        val t = participantService.getFriends(pid)
        t.subscribe {
            Assertions.assertEquals(it.size, tfl.size)
            Assertions.assertEquals(it.first().to, tfl.first().to)
        }
        t.block()
    }

    @Test
    fun addFriend() {
        coEvery {
            friendDao.addFriend(pid, tf)
        } returns Mono.just(tf)
        val t = participantService.addFriend(pid, tf)
        t.subscribe {
            Assertions.assertEquals(it.from, tf.from)
            Assertions.assertEquals(it.to, tf.to)
        }
        t.block()
    }

    @Test
    fun getInbound() {
        coEvery {
            inboundDao.getInbound(pid)
        } returns Mono.just(tib)
        val ib = participantService.getInbound(pid)
        ib.subscribe {
            Assertions.assertEquals(it.size, tib.size)
            Assertions.assertEquals(it.first().subject, tib.first().subject)
        }
        ib.block()
    }

    @Test
    fun getOutbound() {
        coEvery {
            outboundDao.getOutbound(pid)
        } returns Mono.just(tobl)
        val ob = participantService.getOutbound(pid)
        ob.subscribe {
            Assertions.assertEquals(it.size, tobl.size)
            Assertions.assertEquals(it.first().subject, tob.subject)
        }
        ob.block()
    }

    @Test
    fun addOutbound() {
        coEvery {
            outboundDao.addOutbound(pid, tob)
        } returns Mono.just(tob)
        coEvery {
            friendDao.getFriends(pid)
        } returns Mono.just(tfl)
        val ob = participantService.addOutbound(pid, tob)
        ob.subscribe {
            Assertions.assertEquals(it.subject, tob.subject)
        }
        ob.block()
        coVerify {
            inboundDao.addInbound(pid, any())
        }
        coVerify {
            searchDao.indexStory(pid, any())
        }
    }

    @Test
    fun searchOutbound() {
        coEvery {
            searchDao.searchOutbound(any())
        } returns listOf(pid)
        val t = outboundService.searchOutbound("test")
        Assertions.assertEquals(t.size, 1)
        Assertions.assertEquals(t.first(), ParticipantModel(pid, "").link)
    }
}
