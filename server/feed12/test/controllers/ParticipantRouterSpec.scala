import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import play.api.mvc.{ RequestHeader, Result }
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import info.glennengstrand.news.model._
import info.glennengstrand.news.dao._
import scala.util.{Success, Failure}

import scala.concurrent.{ExecutionContext, Future}

class ParticipantRouterSpec extends PlaySpec with GuiceOneAppPerTest {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  "News Feed Router" should {

    "fetch a participant" in {
      val request = FakeRequest(GET, "/participant/1").withHeaders(HOST -> "localhost:8080").withCSRFToken
      val home:Future[Result] = route(app, request).get
      home onComplete {
        case Success(r) => {
          decode[Participant](r.body.toString).map {
            p => p mustBe (MockParticipantDaoImpl.test)
          }
        }
        case Failure(d) => fail(d)
      }
    }
    
    "create a participant" in {
      val request = FakeRequest(POST, "/participant").withHeaders(HOST -> "localhost:8080").withBody(MockParticipantDaoImpl.test.asJson.noSpaces).withCSRFToken
      val home:Future[Result] = route(app, request).get
      home onComplete {
        case Success(r) => {
          decode[Participant](r.body.toString).map {
            p => p mustBe (MockParticipantDaoImpl.test)
          }
        }
        case Failure(d) => fail(d)
      }
    }
    "fetch the friends of a participant" in {
      val request = FakeRequest(GET, "/participant/1/friends").withHeaders(HOST -> "localhost:8080").withCSRFToken
      val home:Future[Result] = route(app, request).get
      home onComplete {
        case Success(r) => {
          decode[Friend](r.body.toString).map {
            p => p mustBe (Seq(MockFriendDaoImpl.test))
          }
        }
        case Failure(d) => fail(d)
      }
    }
    
    "friend two participants" in {
      val request = FakeRequest(POST, "/participant/1/friends").withHeaders(HOST -> "localhost:8080").withBody(MockFriendDaoImpl.test.asJson.noSpaces).withCSRFToken
      val home:Future[Result] = route(app, request).get
      home onComplete {
        case Success(r) => {
          decode[Seq[Friend]](r.body.toString).map {
            p => p mustBe (MockFriendDaoImpl.test)
          }
        }
        case Failure(d) => fail(d)
      }
    }
    
    "fetch the participant's inbound feed" in {
      val request = FakeRequest(GET, "/participant/1/inbound").withHeaders(HOST -> "localhost:8080").withCSRFToken
      val home:Future[Result] = route(app, request).get
      home onComplete {
        case Success(r) => {
          decode[Inbound](r.body.toString).map {
            p => p mustBe (Seq(MockInboundDaoImpl.test))
          }
        }
        case Failure(d) => fail(d)
      }
    }
    
     "fetch the participant's outbound items" in {
      val request = FakeRequest(GET, "/participant/1/outbound").withHeaders(HOST -> "localhost:8080").withCSRFToken
      val home:Future[Result] = route(app, request).get
      home onComplete {
        case Success(r) => {
          decode[Outbound](r.body.toString).map {
            p => p mustBe (Seq(MockOutboundDaoImpl.test))
          }
        }
        case Failure(d) => fail(d)
      }
    }
    
    "create an outbound item for a participant" in {
      val request = FakeRequest(POST, "/participant/1/outbound").withHeaders(HOST -> "localhost:8080").withBody(MockFriendDaoImpl.test.asJson.noSpaces).withCSRFToken
      val home:Future[Result] = route(app, request).get
      home onComplete {
        case Success(r) => {
          decode[Seq[Outbound]](r.body.toString).map {
            p => p mustBe (MockOutboundDaoImpl.test)
          }
        }
        case Failure(d) => fail(d)
      }
    }
    
    "search participants for news item keywords" in {
      val request = FakeRequest(GET, "/outbound?keywords=test").withHeaders(HOST -> "localhost:8080").withCSRFToken
      val home:Future[Result] = route(app, request).get
      home onComplete {
        case Success(r) => {
          decode[Seq[String]](r.body.toString).map {
            p => p mustBe (Seq("/participant/1"))
          }
        }
        case Failure(d) => fail(d)
      }
    }
  }

}
