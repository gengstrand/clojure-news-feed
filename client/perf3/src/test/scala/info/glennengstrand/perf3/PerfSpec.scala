package info.glennengstrand.perf3

import org.scalatest._

class PerfSpec extends FlatSpec with Matchers {
  "an elastic search update request" should "be well formed" in {
    val ( url, body ) = ElasticSearchRequest.generate("localhost", "feed", "20160205T132530.000Z", "outbound", "post", 6000, 200L, 186L, 315L)
    url should be ("http://localhost:9200/performance/feed/outbound-post-20160205T132530.000Z")
    body should be ("{\"ts\":\"20160205T132530.000Z\",\"entity\":\"outbound\",\"operation\":\"post\",\"throughput\":6000,\"mean\":200,\"median\":186,\"upper5\":315}")
  }
  "a performance measurement" should "be able to correctly parse the corresponding kafka message" in {
    val test = PerformanceMeasurement("2015|11|30|18|17|outbound|post|105")
    test.year should be (2015)
    test.month should be (11)
    test.day should be (30)
    test.hour should be (18)
    test.minute should be (17)
    test.entity should be ("outbound")
    test.operation should be ("post")
    test.duration should be (105L)
  }
  "a performance measurement" should "be able to correctly format its state" in {
    val test = PerformanceMeasurement("2015|11|30|18|17|outbound|post|105")
    test.toString() should be ("{\"year\":2015,\"month\":11,\"day\":30,\"hour\":18,\"minute\":17,\"entity\":\"outbound\",\"operation\":\"post\",\"duration\":105}")
  }
}