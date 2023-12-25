package info.glennengstrand.newsfeed.daos

import info.glennengstrand.newsfeed.models.InboundModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class InboundDao {
    val f = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getInbound(id: Long): List<InboundModel> {
        return listOf(
            InboundModel(
                "/participant/2",
                ParticipantModel(id.toLong(), "test").link,
                LocalDate.now().format(f),
                "test subject",
                "test story",
            ),
        )
    }

    fun addInbound(
        id: Long,
        ib: InboundModel,
    ): InboundModel {
        return ib
    }
}
