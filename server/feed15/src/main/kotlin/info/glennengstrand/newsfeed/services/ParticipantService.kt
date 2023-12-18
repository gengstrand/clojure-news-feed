package info.glennengstrand.newsfeed.services

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import info.glennengstrand.newsfeed.models.*

@Component
class ParticipantService {

    val f = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getParticipant(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id")
        return ServerResponse.ok().body(fromValue(ParticipantModel(id.toLong(), "test")))
    }
    fun addParticipant(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(ParticipantModel::class.java)
        .flatMap{p: ParticipantModel -> ServerResponse.ok().body(fromValue(p))}
    }
    fun getFriends(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id")
        val rv = listOf(FriendModel(1L, ParticipantModel(id.toLong(), "test").link, "/participant/2"))
        return ServerResponse.ok().body(fromValue(rv))
    }
    fun addFriend(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(FriendModel::class.java)
        .flatMap{f: FriendModel -> ServerResponse.ok().body(fromValue(f))}
    }
    fun getInbound(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id")
        val rv = listOf(InboundModel("/participant/2", ParticipantModel(id.toLong(), "test").link, LocalDate.now().format(f), "test subject", "test story"))
        return ServerResponse.ok().body(fromValue(rv))
    }
    fun getOutbound(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id")
        val rv = listOf(OutboundModel(ParticipantModel(id.toLong(), "test").link, LocalDate.now().format(f), "test subject", "test story"))
        return ServerResponse.ok().body(fromValue(rv))
    }
    fun addOutbound(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(OutboundModel::class.java)
        .flatMap{f: OutboundModel -> ServerResponse.ok().body(fromValue(f))}
    }

}
