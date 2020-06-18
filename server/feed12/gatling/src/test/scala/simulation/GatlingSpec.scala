package simulation

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.language.postfixOps

// run with "sbt gatling:test" on another machine so you don't have resources contending.
// http://gatling.io/docs/2.2.2/general/simulation_structure.html#simulation-structure
class GatlingSpec extends Simulation {

  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:9000/participant/1")

  val indexReq = repeat(50) {
      exec(http("fetch").get("/").check(status.is(200)))
  }

  val fetchParticipantScenario = scenario("participant").exec(indexReq).pause(1)

  setUp(
    fetchParticipantScenario.inject(rampUsers(20).during(10 seconds)).protocols(httpConf)
  )
}
