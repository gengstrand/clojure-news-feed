package info.glennengstrand.newsfeed.controllers

import info.glennengstrand.newsfeed.models.FriendModel
import info.glennengstrand.newsfeed.models.OutboundModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import info.glennengstrand.newsfeed.services.ParticipantService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.router
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Configuration
class ParticipantController(private val participantService: ParticipantService) {
    @Bean
    fun participantRouter() =
        router {
            GET("/participant/{id}", ::getParticipant)
            POST("/participant/{id}", ::addParticipant)
            GET("/participant/{id}/friends", ::getFriends)
            POST("/participant/{id}/friends", ::addFriend)
            GET("/participant/{id}/inbound", ::getInbound)
            GET("/participant/{id}/outbound", ::getOutbound)
            POST("/participant/{id}/outbound", ::addOutbound)
        }

    fun getParticipant(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return ServerResponse.ok().body(fromValue(participantService.getParticipant(id)))
    }

    fun addParticipant(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return request.bodyToMono(ParticipantModel::class.java)
            .flatMap {
                val p = participantService.addParticipant(id, it)
                ServerResponse.ok().body(fromValue(p))
            }
    }

    fun getFriends(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        val rv = participantService.getFriends(id)
        return ServerResponse.ok().body(fromValue(rv))
    }

    fun addFriend(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return request.bodyToMono(FriendModel::class.java)
            .flatMap {
                val f = participantService.addFriend(id, it)
                ServerResponse.ok().body(fromValue(f))
            }
    }

    fun getInbound(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        val rv = participantService.getInbound(id)
        return ServerResponse.ok().body(fromValue(rv))
    }

    fun getOutbound(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        val rv = participantService.getOutbound(id)
        return ServerResponse.ok().body(fromValue(rv))
    }

    fun addOutbound(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return request.bodyToMono(OutboundModel::class.java)
            .flatMap {
                val ob = participantService.addOutbound(id, it)
                ServerResponse.ok().body(fromValue(ob))
            }
    }

}
