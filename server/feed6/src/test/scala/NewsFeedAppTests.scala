package io.swagger.app

import org.scalatra.test.scalatest._
import org.scalatest.FunSuiteLike
import info.glennengstrand.news.api._
import info.glennengstrand.news.DI._

object NewsFeedAppTests {
  def outboundPost = "{\"from\":\"/participant/1\",\"subject\":\"test\"}"
  def friendPost = "{\"id\":1,\"from\":\"/participant/1\",\"to\":\"/participant/2\"}"
  def friendResult = "[" + friendPost + "]"
  def participantPost = "{\"name\":\"test\"}"
  def participantTest = "test"
}
class NewsFeedAppTests extends ScalatraSuite with FunSuiteLike {
  implicit val swagger = new SwaggerApp

  addServlet(new ParticipantApi, "/participant/*")
  addServlet(new OutboundApi, "/outbound/*")
  test("participant tests") {
    get("/participant/1") {
      status should equal(200)
      body should include(NewsFeedAppTests.participantTest)
    }
    post("/participant", NewsFeedAppTests.participantPost) {
      status should equal(200)
      body should include("test")
    }
  }
  test("friend tests") {
    get("/participant/1/friends") {
      status should equal(200)
      body should equal(NewsFeedAppTests.friendResult)
    }
    post("/participant/1/friends", NewsFeedAppTests.friendPost) {
      status should equal(200)
      body should equal(NewsFeedAppTests.friendPost)
    }
  }
  test("inbound test") {
    get("/participant/1/inbound") {
      status should equal(200)
      body should equal("[]")
    }
  }
  test("outbound tests") {
    get("/participant/1/outbound") {
      status should equal(200)
      body should equal("[]")
    }
    post("/participant/1/outbound", NewsFeedAppTests.outboundPost) {
      status should equal(200)
      body should equal(NewsFeedAppTests.outboundPost)
    }
    get("/outbound?keywords=test") {
      status should equal(200)
      body should equal("[]")
    }
  }
}