package info.glennengstrand.news

import doobie.imports._
import doobie.hikari.HikariTransactor
import com.zaxxer.hikari.{ HikariDataSource, HikariConfig }
import cats.effect._
import org.json4s._
import org.json4s.jackson.Serialization
import info.glennengstrand.news.core._
import info.glennengstrand.news.db._
import org.slf4j.{ Logger, LoggerFactory }
import com.datastax.driver.core.{ Session, Cluster }
import org.elasticsearch.client.{ RestHighLevelClient, RestClient }
import org.apache.http.HttpHost

object DI {
  val logger = LoggerFactory.getLogger(DI.getClass.getCanonicalName)
  val dbHost = sys.env.get("MYSQL_HOST").getOrElse("localhost")
  val dbUser = sys.env.get("MYSQL_USER").getOrElse("feed")
  val dbPass = sys.env.get("MYSQL_PASSWORD").getOrElse("feed1234")
  val cacheHost = sys.env.get("CACHE_HOST").getOrElse("localhost")
  val cachePort = sys.env.get("CACHE_PORT").map(_.toInt).getOrElse(6379)
  val cacheTimeout = sys.env.get("CACHE_TIMEOUT").map(_.toInt).getOrElse(5000)
  val cachePool = sys.env.get("CACHE_POOL").map(_.toInt).getOrElse(10)
  val noSqlHost = sys.env.get("NOSQL_HOST").getOrElse("localhost")
  val noSqlTtl = sys.env.get("NOSQL_TTL").getOrElse(1000 * 60 * 60 * 24)
  val noSqlKeyspace = sys.env.get("NOSQL_KEYSPACE").getOrElse("feed")
  val searchHost = sys.env.get("SEARCH_HOST").getOrElse("localhost")
  val testMode = sys.env.get("TEST_MODE").getOrElse("false").toBoolean
  val jdbcConnect = "jdbc:mysql://" + dbHost + ":3306/feed"
  implicit val db: Transactor[IO] = testMode match {
    case true => null
    case false => {
      val config = new HikariConfig
      config.setJdbcUrl(jdbcConnect)
      config.setDriverClassName("com.mysql.jdbc.Driver")
      config.setUsername(dbUser)
      config.setPassword(dbPass)
      HikariTransactor[IO](new HikariDataSource(config))
    }
  }
  implicit val session: Session = testMode match {
    case true => null
    case false => {
      val b = Cluster.builder().addContactPoint(noSqlHost)
      b.build().connect(noSqlKeyspace)
    }
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
  implicit val inboundDAO = testMode match {
    case true => new MockInboundDAO
    case false => InboundDAO(session)
  }
  implicit val outboundDAO = testMode match {
    case true => new MockOutboundItemDAO
    case false => OutboundItemDAO(session)
  }
  lazy val searchClient = testMode match {
    case true => null
    case false => new RestHighLevelClient(RestClient.builder(new HttpHost(searchHost, 9200)))
  }
  lazy val searchDAO = testMode match {
    case true => new MockOutboundDocumentDAO
    case false => new OutboundDocumentDAO(searchClient)
  }
  lazy val participantService = new ParticipantService
  lazy val friendService = new FriendService
  lazy val inboundService = new InboundService
  lazy val outboundService = new OutboundService(friendService, inboundService, searchDAO)
}
