package io.swagger.app

import org.scalatra.test.scalatest._
import org.scalatest.FunSuiteLike
import info.glennengstrand.news.api._
import info.glennengstrand.news.DI._

object NewsFeedAppTests {
  def outboundPost = "{\"from\":1,\"subject\":\"test\"}"
  def friendPost = "{\"id\":1,\"from\":1,\"to\":2}"
  def friendResult = "[" + friendPost + "]"
  def participantPost = "{\"name\":\"test\"}"
  def participantTest = "test"
}
class NewsFeedAppTests extends ScalatraSuite with FunSuiteLike {
  implicit val swagger = new SwaggerApp

  addServlet(new ParticipantApi, "/participant/*")
  addServlet(new FriendApi, "/friends/*")
  addServlet(new InboundApi, "/inbound/*")
  addServlet(new OutboundApi, "/outbound/*")
  test("participant tests") {
    get("/participant/1") {
      status should equal(200)
      body should include(NewsFeedAppTests.participantTest)
    }
    post("/participant/new", NewsFeedAppTests.participantPost) {
      status should equal(200)
      body should include("test")
    }
  }
  test("friend tests") {
    get("/friends/1") {
      status should equal(200)
      body should equal(NewsFeedAppTests.friendResult)
    }
    post("/friends/new", NewsFeedAppTests.friendPost) {
      status should equal(200)
      body should equal(NewsFeedAppTests.friendPost)
    }
  }
  test("imbound test") {
    get("/inbound/1") {
      status should equal(200)
      body should equal("[]")
    }
  }
  test("outbound tests") {
    get("/outbound/1") {
      status should equal(200)
      body should equal("[]")
    }
    post("/outbound/new", NewsFeedAppTests.outboundPost) {
      status should equal(200)
      body should equal(NewsFeedAppTests.outboundPost)
    }
  }
}