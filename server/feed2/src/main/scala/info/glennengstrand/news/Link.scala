package info.glennengstrand.news

object Link {
  val path = raw"/participant/([0-9]+)".r
  def extractId(link: String): Long = {
    link match {
      case path(id) => id.toLong
      case _ => link.toLong
    }
  }
  def toLink(id: Long): String = {
    "/participant/" + id.toString
  }
}