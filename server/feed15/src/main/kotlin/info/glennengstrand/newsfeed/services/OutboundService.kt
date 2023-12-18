package info.glennengstrand.newsfeed.services

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class OutboundService {
    fun searchOutbound(request: ServerRequest): Mono<ServerResponse> {
        val keywords = request.queryParam("keywords")
        val rv = listOf("/participant/2")
        return ServerResponse.ok().body(fromValue(rv))
    }
}