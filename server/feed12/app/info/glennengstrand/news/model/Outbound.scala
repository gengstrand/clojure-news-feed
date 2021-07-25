package info.glennengstrand.news.model

import info.glennengstrand.news.dao.Link

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
  def source(l: Link): Map[String, Object] = {
    val retVal = for {
      from <- from
      story <- story
    } yield Map("sender" -> l.extractId(from), "story" -> story)
    if (retVal.isEmpty) {
      Map()
    } else {
      retVal.head.asInstanceOf[Map[String, Object]]
    }
  }
}
