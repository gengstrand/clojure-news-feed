package info.glennengstrand.news.model

case class Outbound(
  from: Option[String],
  occurred: Option[String],
  subject: Option[String],
  story: Option[String])
