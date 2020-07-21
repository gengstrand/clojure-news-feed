package simulation

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

/* http://gatling.io/docs/2.2.2/general/simulation_structure.html#simulation-structure
   used for local Java profiling 
   see https://github.com/gengstrand/clojure-news-feed/tree/master/client/load for the real load test */
class GatlingSpec extends Simulation {

  val r = Random
  val rpt = 4
  def toLink(id: Int): String = {
    "/participant/" + id.toString
  }
  def friendParticipantBody(fid: String): String = {
      val tid = toLink(r.nextInt(rpt) + 1)
      "{\"from\":\"" + fid + "\",\"to\":\"" + tid + "\"}"
  }
  def createNewsItemBody(fid: String): String = {
      "{\"from\":\"" + fid + "\",\"subject\":\"testing\",\"story\":\"load test\"}"
  }
  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:9000")

  val createParticipantReq = repeat(rpt) {
      exec(http("create-participant").post("/participant").header(HttpHeaderNames.ContentType, HttpHeaderValues.ApplicationJson).body(StringBody("{\"name\":\"test\"}")).check(status.is(200)))
  }
  val friendParticipantReq = repeat(3 * rpt) {
      val fid = toLink(r.nextInt(rpt) + 1)
      exec(http("create-friend").post(fid + "/friends").header(HttpHeaderNames.ContentType, HttpHeaderValues.ApplicationJson).body(StringBody(friendParticipantBody(fid))).check(status.is(200)))
  }
  val createNewsReq = repeat(rpt * rpt) {
      val fid = toLink(r.nextInt(rpt) + 1)
      exec(http("create-outbound").post(fid + "/outbound").header(HttpHeaderNames.ContentType, HttpHeaderValues.ApplicationJson).body(StringBody(createNewsItemBody(fid))).check(status.is(200)))
  }
  val participantScenario = scenario("load-for-profiling")
  .exec(createParticipantReq)
  .pause(20)
  .exec(friendParticipantReq)
  .pause(20)
  .exec(createNewsReq)

  setUp(
    participantScenario.inject(rampUsers(rpt).during(10 seconds)).protocols(httpConf)
  )
}
