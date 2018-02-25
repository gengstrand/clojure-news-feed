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

package info.glennengstrand.news.model
import java.util.Date

case class Inbound(
  from: Option[Long],
  to: Option[Long],
  occurred: Option[Date],
  subject: Option[String],
  story: Option[String])