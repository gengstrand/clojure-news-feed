package info.glennengstrand.newsfeed.services

import info.glennengstrand.newsfeed.daos.OutboundDao
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class OutboundService (val outboundDao: OutboundDao) {
    fun searchOutbound(keywords: String): List<String> {
        return outboundDao.searchOutbound(keywords).map{"/participant/$it"}
    }
}
