package info.glennengstrand.news.dao

import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future
import info.glennengstrand.news.model.Friend

class FriendDao extends DataAccess[Friend] {
  override def fetchSingle(id: Int): Future[Friend] = {
    Future {
      Friend(None, None, None)
    }
  }
  override def fetchMulti(id: Int): Future[Seq[Friend]] = {
    val s = sql"call FetchFriends($id)".as[(Long, Long)]
    MySqlDao.db.run(s.map(results => {
      for {
        friend <- results
      } yield Friend(Option(friend._1), Option("/participant/%d".format(id)), Option("/participant/%d".format(friend._2)))
    }))
  }
  override def insert(f: Friend): Future[Friend] = {
    val s = sql"call UpsertFriends(${f.from.get}, ${f.to.get})".as[Int]
    MySqlDao.db.run(s.map(r => Friend(Option(r(0)), f.from, f.to)))
  }
}