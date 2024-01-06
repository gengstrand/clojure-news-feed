package info.glennengstrand.newsfeed.controllers

import info.glennengstrand.newsfeed.models.FriendModel
import info.glennengstrand.newsfeed.models.OutboundModel
import info.glennengstrand.newsfeed.models.ParticipantModel
import info.glennengstrand.newsfeed.services.ParticipantService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class ParticipantController(private val participantService: ParticipantService) {
    @Bean
    fun participantRouter() =
        router {
            GET("/participant/{id}", ::getParticipant)
            POST("/participant", ::addParticipant)
            GET("/participant/{id}/friends", ::getFriends)
            POST("/participant/{id}/friends", ::addFriend)
            GET("/participant/{id}/inbound", ::getInbound)
            GET("/participant/{id}/outbound", ::getOutbound)
            POST("/participant/{id}/outbound", ::addOutbound)
        }

    fun getParticipant(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return participantService.getParticipant(id)
            .flatMap { ServerResponse.ok().body(fromValue(it)) }
    }

    fun addParticipant(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(ParticipantModel::class.java)
            .flatMap { participantService.addParticipant(it) }
            .flatMap { ServerResponse.ok().body(fromValue(it)) }
    }

    fun getFriends(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return participantService.getFriends(id)
            .flatMap { ServerResponse.ok().body(fromValue(it)) }
    }

    fun addFriend(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return request.bodyToMono(FriendModel::class.java)
            .flatMap { participantService.addFriend(id, it) }
            .flatMap { ServerResponse.ok().body(fromValue(it)) }
    }

    fun getInbound(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return participantService.getInbound(id)
            .flatMap { ServerResponse.ok().body(fromValue(it)) }
    }

    fun getOutbound(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return participantService.getOutbound(id)
            .flatMap { ServerResponse.ok().body(fromValue(it)) }
    }

    fun addOutbound(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLongOrNull()
        if (id == null) return ServerResponse.badRequest().body(fromValue("invalid id"))
        return request.bodyToMono(OutboundModel::class.java)
            .flatMap { participantService.addOutbound(id, it) }
            .flatMap { ServerResponse.ok().body(fromValue(it)) }
    }
}
