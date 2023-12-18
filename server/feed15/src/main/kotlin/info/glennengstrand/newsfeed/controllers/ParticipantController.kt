package info.glennengstrand.newsfeed.controllers

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router
import info.glennengstrand.newsfeed.services.ParticipantService

@Configuration
class ParticipantController(private val participantService: ParticipantService) {

      @Bean
      fun participantRouter() = router {
          GET("/participant/{id}", participantService::getParticipant)
          POST("/participant/{id}", participantService::addParticipant)
          GET("/participant/{id}/friends", participantService::getFriends)
          POST("/participant/{id}/friends", participantService::addFriend)
          GET("/participant/{id}/inbound", participantService::getInbound)
          GET("/participant/{id}/outbound", participantService::getOutbound)
          POST("/participant/{id}/outbound", participantService::addOutbound)
      }
}