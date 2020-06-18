import javax.inject._

import play.api.Mode
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import info.glennengstrand.news.dao._

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with ScalaModule {

  override def configure() = {
    environment.mode match {
      case Mode.Test => {
        bind[ParticipantDao].to[MockParticipantDaoImpl].in[Singleton]
        bind[FriendDao].to[MockFriendDaoImpl].in[Singleton]
        bind[InboundDao].to[MockInboundDaoImpl].in[Singleton]
        bind[OutboundDao].to[MockOutboundDaoImpl].in[Singleton]
        bind[SearchDao].to[MockSearchDaoImpl].in[Singleton]
      }
      case _ => {
        bind[ParticipantDao].to[ParticipantDaoImpl].in[Singleton]
        bind[FriendDao].to[FriendDaoImpl].in[Singleton]
        bind[InboundDao].to[InboundDaoImpl].in[Singleton]
        bind[OutboundDao].to[OutboundDaoImpl].in[Singleton]
        bind[SearchDao].to[SearchDaoImpl].in[Singleton]
      }
    }
  }
}
