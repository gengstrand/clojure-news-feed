package info.glennengstrand.newsfeed.controllers

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.router
import info.glennengstrand.newsfeed.services.OutboundService

@Configuration
class OutboundController(private val outboundService: OutboundService) {

      @Bean
      fun outboundRouter() = router {
          GET("/outbound", outboundService::searchOutbound)
      }
}
