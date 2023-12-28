package info.glennengstrand.newsfeed.daos

import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class SearchDao {
    private val logger = KotlinLogging.logger {}

    fun indexStory(
        id: Long,
        story: String,
    ) {
        logger.info("indexing $story for participant $id")
    }

    fun searchOutbound(keywords: String): List<Long> {
        logger.info("searching for $keywords")
        return listOf(1L)
    }
}
