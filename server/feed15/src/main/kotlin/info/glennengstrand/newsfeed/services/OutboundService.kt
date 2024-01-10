package info.glennengstrand.newsfeed.services

import info.glennengstrand.newsfeed.daos.SearchDao
import info.glennengstrand.newsfeed.models.ParticipantModel
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OutboundService(
    val searchDao: SearchDao,
) {
    fun searchOutbound(keywords: String): Mono<List<String>> {
        return searchDao.searchOutbound(keywords)
            .flatMap {
                Mono.just(it.map { ParticipantModel(it, "").link })
            }
    }
}
