package info.glennengstrand.newsfeed.services

import info.glennengstrand.newsfeed.daos.OutboundDao
import org.springframework.stereotype.Component

@Component
class OutboundService(val outboundDao: OutboundDao) {
    fun searchOutbound(keywords: String): List<String> {
        return outboundDao.searchOutbound(keywords).map { "/participant/$it" }
    }
}
