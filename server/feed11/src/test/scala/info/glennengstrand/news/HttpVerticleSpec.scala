package info.glennengstrand.news

import org.scalatest.Matchers

import scala.concurrent.Promise

class HttpVerticleSpec extends VerticleTesting[HttpVerticle] with Matchers {

  "HttpVerticle" should "serve get participant endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient()
      .getNow(8080, "127.0.0.1", "/participant/1",
        r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
        })

    promise.future.map(res => res should equal("{\"id\":1,\"name\":\"test\",\"link\":\"/participant/1\"}"))
  }
  
  "HttpVerticle" should "serve create participant endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient().post(8080, "127.0.0.1", "/participant")
      .handler(r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
      })
      .end("{\"name\":\"test\"}")

    promise.future.map(res => res should equal("{\"id\":null,\"name\":\"test\",\"link\":null}"))
  }

  "HttpVerticle" should "serve get friends endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient()
      .getNow(8080, "127.0.0.1", "/participant/1/friends",
        r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
        })

    promise.future.map(res => res should equal("[{\"id\":1,\"from\":\"/participant/1\",\"to\":\"/participant/2\"}]"))
  }
  
  "HttpVerticle" should "serve create friend endpoint" in {
    val promise = Promise[String]

    
    vertx.createHttpClient().post(8080, "127.0.0.1", "/participant/1/friends")
      .handler(r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
      })
      .end("{\"from\":\"/participant/1\",\"to\":\"/participant/2\"}")

    promise.future.map(res => res should equal("{\"id\":null,\"from\":\"/participant/1\",\"to\":\"/participant/2\"}"))
  }
  
  "HttpVerticle" should "serve get inbound endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient()
      .getNow(8080, "127.0.0.1", "/participant/2/inbound",
        r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
        })

    promise.future.map(res => res should equal("[{\"from\":\"/participant/1\",\"to\":\"/participant/2\",\"occurred\":null,\"subject\":\"test subject\",\"story\":\"test story\"}]"))
  }
  
  "HttpVerticle" should "serve get outbound endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient()
      .getNow(8080, "127.0.0.1", "/participant/1/outbound",
        r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
        })

    promise.future.map(res => res should equal("[{\"from\":\"/participant/1\",\"occurred\":null,\"subject\":\"test subject\",\"story\":\"test story\"}]"))
  }
  
  "HttpVerticle" should "serve create outbound endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient().post(8080, "127.0.0.1", "/participant/1/outbound")
      .handler(r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
      })
      .end("{\"from\":\"/participant/1\",\"occurred\":null,\"subject\":\"test subject\",\"story\":\"test story\"}")

    promise.future.map(res => res should equal("{\"from\":\"/participant/1\",\"occurred\":null,\"subject\":\"test subject\",\"story\":\"test story\"}"))
  }
  
  "HttpVerticle" should "serve search outbound endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient()
      .getNow(8080, "127.0.0.1", "/outbound?keywords=test",
        r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
        })

    promise.future.map(res => res should equal("[\"/participant/1\"]"))
  }
}
