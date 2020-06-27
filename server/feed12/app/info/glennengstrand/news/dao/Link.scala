package info.glennengstrand.news.dao

trait Link {
  private val path = raw"/participant/([0-9]+)".r
  def toLink(id: Long): String = {
    "/participant/%d".format(id)
  }
  def extractId(link: String): Long = {
    link match {
      case path(id) => id.toLong
      case _ => link.toLong
    }
  }
}