package info.glennengstrand.newsfeed.controllers

import info.glennengstrand.newsfeed.services.OutboundService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class OutboundController(private val outboundService: OutboundService) {
    @Bean
    fun outboundRouter() =
        router {
            GET("/outbound", ::searchOutbound)
        }

    fun searchOutbound(request: ServerRequest): Mono<ServerResponse> {
        val keywords = request.queryParam("keywords")
        if (keywords.isEmpty()) return ServerResponse.badRequest().body(fromValue("keywords missing"))
        val rv = outboundService.searchOutbound(keywords.get())
        return ServerResponse.ok().body(fromValue(rv))
    }
}
