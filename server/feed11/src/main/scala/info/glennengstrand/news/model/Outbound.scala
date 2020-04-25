package info.glennengstrand.news.model

case class Outbound(
  from: Option[String],
  occurred: Option[String],
  subject: Option[String],
  story: Option[String]) {
  def isValid: Boolean = {
    val rv = for {
      f <- from
      sb <- subject
      st <- story
    } yield (f, sb, st)
    !rv.isEmpty
  }
}
