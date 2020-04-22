package info.glennengstrand.news.model

case class Inbound(
  from: Option[String],
  to: Option[String],
  occurred: Option[String],
  subject: Option[String],
  story: Option[String])
