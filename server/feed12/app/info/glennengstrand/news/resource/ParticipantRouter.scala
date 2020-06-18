package info.glennengstrand.news.resource

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import info.glennengstrand.news.model._

/**
  * Routes and URLs to the PostResource controller.
  */
class ParticipantRouter @Inject()(pc: ParticipantController, fc: FriendController, ic: InboundController, oc: OutboundController) extends SimpleRouter {
  val prefix = "/participant"

  def link(id: Int): String = {
    import io.lemonlabs.uri.dsl._
    val url = prefix + "/" + id.toString
    url.toString()
  }

  override def routes: Routes = {
    
    case POST(p"/") => pc.create

    case POST(p"/$id/friends") => fc.create(id.toInt)

    case POST(p"/$id/outbound") => oc.create(id.toInt)

    case GET(p"/$id") => pc.get(id.toInt)

    case GET(p"/$id/friends") => fc.get(id.toInt)

    case GET(p"/$id/inbound") => ic.get(id.toInt)

    case GET(p"/$id/outbound") => oc.get(id.toInt)

  }

}
