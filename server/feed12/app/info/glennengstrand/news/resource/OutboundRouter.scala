package info.glennengstrand.news.resource

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import info.glennengstrand.news.model._

/**
  * Routes and URLs to the PostResource controller.
  */
class OutboundRouter @Inject()(oc: OutboundController) extends SimpleRouter {
  val prefix = "/outbound"

  def link(id: Int): String = {
    import io.lemonlabs.uri.dsl._
    val url = prefix + "/" + id.toString
    url.toString()
  }

  override def routes: Routes = {
    
    case GET(p"/") => oc.search()

  }

}
