package info.glennengstrand.newsfeed

import info.glennengstrand.newsfeed.daos.FriendDao
import info.glennengstrand.newsfeed.daos.InboundDao
import info.glennengstrand.newsfeed.daos.OutboundDao
import info.glennengstrand.newsfeed.daos.ParticipantDao
import info.glennengstrand.newsfeed.models.FriendModel
import info.glennengstrand.newsfeed.models.InboundModel
import info.glennengstrand.newsfeed.models.OutboundModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import info.glennengstrand.newsfeed.services.ParticipantService
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

// import io.mockk.coVerify
// import io.mockk.slot

@SpringBootTest
class NewsfeedApplicationTests {
    private val participantDao = mockk<ParticipantDao>(relaxed = true)
    private val friendDao = mockk<FriendDao>(relaxed = true)
    private val inboundDao = mockk<InboundDao>(relaxed = true)
    private val outboundDao = mockk<OutboundDao>(relaxed = true)
    private val participantService = ParticipantService(participantDao, friendDao, inboundDao, outboundDao)
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
        } returns tp
        val p = participantService.getParticipant(pid)
        Assertions.assertEquals(p.name, tp.name)
    }

    @Test
    fun addParticipant() {
        coEvery {
            participantDao.addParticipant(pid, tp)
        } returns tp
        val p = participantService.addParticipant(pid, tp)
        Assertions.assertEquals(p.name, tp.name)
    }

    @Test
    fun getFriends() {
        coEvery {
            friendDao.getFriends(pid)
        } returns tfl
        val f = participantService.getFriends(pid)
        Assertions.assertEquals(f.size, tfl.size)
        Assertions.assertEquals(f.first().to, tfl.first().to)
    }

    @Test
    fun addFriend() {
        coEvery {
            friendDao.addFriend(pid, tf)
        } returns tf
        val f = participantService.addFriend(pid, tf)
        Assertions.assertEquals(f.from, tf.from)
        Assertions.assertEquals(f.to, tf.to)
    }

    @Test
    fun getInbound() {
        coEvery {
            inboundDao.getInbound(pid)
        } returns tib
        val ib = participantService.getInbound(pid)
        Assertions.assertEquals(ib.size, tib.size)
        Assertions.assertEquals(ib.first().subject, tib.first().subject)
    }

    @Test
    fun getOutbound() {
        coEvery {
            outboundDao.getOutbound(pid)
        } returns tobl
        val ob = participantService.getOutbound(pid)
        Assertions.assertEquals(ob.size, tobl.size)
        Assertions.assertEquals(ob.first().subject, tob.subject)
    }

    @Test
    fun addOutbound() {
        coEvery {
            outboundDao.addOutbound(pid, tob)
        } returns tob
        coEvery {
            friendDao.addFriend(pid, tf)
        } returns tf
        val ob = participantService.addOutbound(pid, tob)
        Assertions.assertEquals(ob.subject, tob.subject)
    }
}
