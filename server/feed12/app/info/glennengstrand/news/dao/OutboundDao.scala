package info.glennengstrand.news.dao

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import scala.jdk.CollectionConverters._

import com.datastax.oss.driver.api.core.cql.{BoundStatement, PreparedStatement}
import com.datastax.oss.driver.api.core.CqlSession

import scala.concurrent.Future
import info.glennengstrand.news.model._

class OutboundExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait OutboundDao {
  def create(id: Int, data: Outbound)(implicit mc: MarkerContext): Future[Outbound]

  def get(id: Int)(implicit mc: MarkerContext): Future[Seq[Outbound]]
  
  def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]]
  
}

@Singleton
class OutboundDaoImpl @Inject()(searchDao: SearchDao, nosql: NoSqlDao)(implicit ec: OutboundExecutionContext)
    extends OutboundDao with Link {

  private val logger = Logger(this.getClass)
  private lazy val session: CqlSession = connect
  private val selectCql = "select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc"
  private val insertCql = "insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)"
  private lazy val insertStmt = session.prepare(insertCql)
  private lazy val selectStmt = session.prepare(selectCql)

  private def connect: CqlSession = {
    var rv: CqlSession = null
    nosql.connect(s => rv = s)
    rv
  }
  
  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Outbound]] = {
    Future {
      val bs = selectStmt.bind(id.asInstanceOf[Object])
      val retVal = for {
        r <- session.execute(bs).iterator().asScala
      } yield Outbound(Option(toLink(id.toLong)), Option(r.getInstant(0).toString()), Option(r.getString(1)), Option(r.getString(2)))
      retVal.toSeq
    }
  }

  override def create(id: Int, data: Outbound)(implicit mc: MarkerContext): Future[Outbound] = {
    Future {
      val bs = insertStmt.bind(new java.lang.Integer(extractId(data.from.get.toString.asInstanceOf[String]).toInt), 
          data.subject.get.toString.asInstanceOf[String], 
          data.story.get.toString.asInstanceOf[String])
      session.execute(bs)
      searchDao.index(data.source)
      data
    }
  }
  
  override def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]] = {
    searchDao.search(keywords)
  }

}

object MockOutboundDaoImpl {
  val test = Outbound(
      from = Option("/participant/1"),
      occurred = Option("2020/06/14"),
      subject = Option("test subject"),
      story = Option("test story")
  )
}

@Singleton
class MockOutboundDaoImpl @Inject()()(implicit ec: OutboundExecutionContext)
    extends OutboundDao {

  private val logger = Logger(this.getClass)

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Outbound]] = {
    Future {
      logger.trace(s"get: id = $id")
      Seq(MockOutboundDaoImpl.test)
    }
  }

  override def create(id: Int, data: Outbound)(implicit mc: MarkerContext): Future[Outbound] = {
    Future {
      logger.trace(s"create: data = $data")
      data
    }
  }
  
  override def search(keywords: String)(implicit mc: MarkerContext): Future[Seq[String]] = {
    Future {
      Seq("/participant/1")
    }
  }

}
