package info.glennengstrand.news.model

case class Inbound(
  from: Option[String],
  to: Option[String],
  occurred: Option[String],
  subject: Option[String],
  story: Option[String]) {
  def isValid: Boolean = {
    val rv = for {
      f <- from
      t <- to
      sb <- subject
      st <- story
    } yield (f, t, sb, st)
    !rv.isEmpty
  }
}
