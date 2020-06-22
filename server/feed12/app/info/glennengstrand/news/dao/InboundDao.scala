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

class InboundExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait InboundDao {
  def create(id: Int, data: Inbound)(implicit mc: MarkerContext): Future[Inbound]

  def get(id: Int)(implicit mc: MarkerContext): Future[Seq[Inbound]]
}

@Singleton
class InboundDaoImpl @Inject()(nosql: NoSqlDao)(implicit ec: InboundExecutionContext)
    extends InboundDao with Link {

  private val logger = Logger(this.getClass)
  private val selectCql = "select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc"
  private val insertCql = "insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?)"
  private lazy val session: CqlSession = connect
  private lazy val insertStmt = session.prepare(insertCql)
  private lazy val selectStmt = session.prepare(selectCql)

  private def connect: CqlSession = {
    var rv: CqlSession = null
    nosql.connect(s => rv = s)
    rv
  }
  
  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Inbound]] = {
    Future {
      val bs = selectStmt.bind(id.asInstanceOf[Object])
      val retVal = for {
        r <- session.execute(bs).iterator().asScala
      } yield Inbound(Option(toLink(r.getInt(1).toLong)), Option(toLink(id.toLong)), Option(r.getInstant(0).toString()), Option(r.getString(2)), Option(r.getString(3)))
      retVal.toSeq
    }
  }

  def create(id: Int, data: Inbound)(implicit mc: MarkerContext): Future[Inbound] = {
    Future {
      val bs = insertStmt.bind(new java.lang.Integer(extractId(data.to.get.toString.asInstanceOf[String]).toInt), 
          new java.lang.Integer(extractId(data.from.get.toString.asInstanceOf[String]).toInt), 
          data.subject.get.toString.asInstanceOf[String], 
          data.story.get.toString.asInstanceOf[String])
      session.execute(bs)
      data
    }
  }

}

object MockInboundDaoImpl {
  val test = Inbound(
      from = Option("/participant/1"),
      to = Option("/participant/2"),
      occurred = Option("2020/06/14"),
      subject = Option("test subject"),
      story = Option("test story")
  )
}

@Singleton
class MockInboundDaoImpl @Inject()()(implicit ec: InboundExecutionContext)
    extends InboundDao {

  override def get(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Inbound]] = {
    Future {
      Seq(MockInboundDaoImpl.test)
    }
  }

  override def create(id: Int, data: Inbound)(implicit mc: MarkerContext): Future[Inbound] = {
    Future {
      data
    }
  }

}
