package info.glennengstrand.news

import doobie.imports._
import cats.effect._
import org.json4s._
import org.json4s.jackson.Serialization
import info.glennengstrand.news.core._
import info.glennengstrand.news.db._

object DI {
  val dbHost = sys.env.get("MYSQL_HOST").getOrElse("localhost")
  val dbUser = sys.env.get("MYSQL_USER").getOrElse("feed")
  val dbPass = sys.env.get("MYSQL_PASSWORD").getOrElse("feed1234")
  val cacheHost = sys.env.get("CACHE_HOST").getOrElse("localhost")
  val cachePort = sys.env.get("CACHE_PORT").map(_.toInt).getOrElse(6379)
  val cacheTimeout = sys.env.get("CACHE_TIMEOUT").map(_.toInt).getOrElse(5000)
  val cachePool = sys.env.get("CACHE_POOL").map(_.toInt).getOrElse(10)
  val testMode = sys.env.get("TEST_MODE").getOrElse("false").toBoolean
  implicit val db: Transactor[IO] = testMode match {
    case true => null
    case false => Transactor.fromDriverManager[IO]("com.mysql.jdbc.Driver", "jdbc:mysql://${dbHost}/feed", dbUser, dbPass)
  }
  implicit val formats = Serialization.formats(NoTypeHints)
  implicit val cache: Cache = testMode match {
    case true => new MockCache
    case false => RedisCache(cacheHost, cachePort, cacheTimeout, cachePool)
  }
  implicit val participantDAO = testMode match {
    case true => new MockParticipantDAO
    case false => new ParticipantDAO
  }
  implicit val friendDAO = testMode match {
    case true => new MockFriendDAO
    case false => new FriendDAO
  }
  lazy val participantService = new ParticipantService
  lazy val friendService = new FriendService
  lazy val inboundService = new InboundService
  lazy val outboundService = new OutboundService
}
