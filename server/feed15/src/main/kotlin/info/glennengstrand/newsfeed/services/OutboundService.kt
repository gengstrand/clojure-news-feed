package info.glennengstrand.newsfeed.services

import info.glennengstrand.newsfeed.daos.SearchDao
import info.glennengstrand.newsfeed.models.ParticipantModel
import org.springframework.stereotype.Component

@Component
class OutboundService(
    val searchDao: SearchDao,
) {
    fun searchOutbound(keywords: String): List<String> {
        return searchDao.searchOutbound(keywords).map { ParticipantModel(it, "").link }
    }
}
