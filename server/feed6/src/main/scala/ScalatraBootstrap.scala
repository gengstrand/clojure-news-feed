/**
 * News Feed
 * news feed api
 *
 * OpenAPI spec version: 1.0.0
 * Contact: media@glennengstrand.info
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 */

import info.glennengstrand.news.api._
import io.swagger.app.{ ResourcesApp, SwaggerApp }
import javax.servlet.ServletContext
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle {
  implicit val swagger = new SwaggerApp

  override def init(context: ServletContext) {
    try {
      context mount (new FriendApi, "/friends/*")
      context mount (new InboundApi, "/inbound/*")
      context mount (new OutboundApi, "/outbound/*")
      context mount (new ParticipantApi, "/participant/*")

      context mount (new ResourcesApp, "/api-docs/*")
    } catch {
      case e: Throwable => e.printStackTrace()
    }
  }
}