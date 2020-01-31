package info.glennengstrand.news.db;

import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.data._
import cats.implicits._
import info.glennengstrand.news.model.Friend
import info.glennengstrand.news.core.EntityDAO
import info.glennengstrand.news.Link

class FriendDAO extends EntityDAO[Friend] {
  def get(id: Long)(implicit db: Transactor[IO]): Option[Friend] = {
    None
  }
  def gets(id: Long)(implicit db: Transactor[IO]): List[Friend] = {
    sql"call FetchFriends(${id})"
      .query[(Long, Long)]
      .to[List]
      .transact(db)
      .unsafeRunSync
      .map(f => Friend(Option(id), Option(Link.toLink(f._1)), Option(Link.toLink(f._2))))
  }
  def add(friend: Friend)(implicit db: Transactor[IO]): Friend = {
    val retVal = for {
      from <- friend.from
      to <- friend.to
      val result = sql"call UpsertFriends(${Link.extractId(from)}, ${Link.extractId(to)})"
        .query[Long]
        .to[List]
        .transact(db)
        .unsafeRunSync
        .take(1)
    } yield (result.head, from, to)
    retVal match {
      case Some((i, f, t)) => Friend(Option(i), Option(f), Option(t))
      case None => Friend(None, None, None)
    }
  }
}
class MockFriendDAO extends EntityDAO[Friend] {
  def get(id: Long)(implicit db: Transactor[IO]): Option[Friend] = {
    None
  }
  def gets(id: Long)(implicit db: Transactor[IO]): List[Friend] = {
    List(Friend(Option(1L), Option(Link.toLink(1L)), Option(Link.toLink(2L))))
  }
  def add(friend: Friend)(implicit db: Transactor[IO]): Friend = {
    friend
  }
}
