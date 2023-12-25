package info.glennengstrand.newsfeed.daos

import info.glennengstrand.newsfeed.models.OutboundModel
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class OutboundDao {
    private val f = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val logger = KotlinLogging.logger {}

    fun getOutbound(id: Long): List<OutboundModel> {
        return listOf(
            OutboundModel(
                "/participant/2",
                LocalDate.now().format(f),
                "test subject",
                "test story",
            ),
        )
    }

    fun addOutbound(
        id: Long,
        ib: OutboundModel,
    ): OutboundModel {
        return ib
    }

    fun searchOutbound(keywords: String): List<Long> {
        logger.info("searching for $keywords")
        return listOf(1L)
    }
}
