package info.glennengstrand.news.dao

import slick.jdbc.MySQLProfile.api._

object MySqlDao {
   lazy val db = Database.forConfig("mysql")
}
