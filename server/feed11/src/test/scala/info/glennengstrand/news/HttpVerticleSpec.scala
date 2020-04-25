package info.glennengstrand.news

import org.scalatest.Matchers
import info.glennengstrand.news.dao._
import info.glennengstrand.news.model._
import info.glennengstrand.news.service._
import scala.concurrent.{Future, Promise}

class HttpVerticleSpec extends VerticleTesting[HttpVerticle] with Matchers {

  class ParticipantDaoMock extends ParticipantDao {
    override def fetchSingle(id: Int): Future[Participant] = {
      Future {
        Participant(Option(1), Option("test"), Option("/participant/%d".format(1)))
      }
    }
    override def insert(p: Participant): Future[Participant] = {
      Future {
        Participant(None, Option("test"), None)
      }
    }
  }
  
  class FriendDaoMock extends FriendDao {
    override def fetchMulti(id: Int): Future[Seq[Friend]] = {
      Future {
        Seq(Friend(Option(1), Option("/participant/%d".format(id)), Option("/participant/2")))
      }
    }
    override def insert(f: Friend): Future[Friend] = {
      Future {
        f
      }
    }
  }
  
  class CacheMock extends CacheWrapper {
    def get(key: String): Option[Object] = {
      None
    }
    def put(key: String, value: Object): Unit = {
      
    }
    def del(key: String): Unit = {
      
    }
    def close: Unit = {
      
    }
  }
  
  "Participant Get" should "serve get participant endpoint" in {
    ParticipantService.dao = new ParticipantDaoMock
    ParticipantService.cache = new CacheMock
    val promise = Promise[String]

    vertx.createHttpClient()
      .getNow(8080, "127.0.0.1", "/participant/1",
        r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
        })

    promise.future.map(res => res should equal("{\"id\":1,\"name\":\"test\",\"link\":\"/participant/1\"}"))
  }
  
  "Participant Create" should "serve create participant endpoint" in {
    ParticipantService.dao = new ParticipantDaoMock
    ParticipantService.cache = new CacheMock
    val promise = Promise[String]

    vertx.createHttpClient().post(8080, "127.0.0.1", "/participant")
      .handler(r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
      })
      .end("{\"name\":\"test\"}")

    promise.future.map(res => res should equal("{\"id\":null,\"name\":\"test\",\"link\":null}"))
  }

  "Friends Get" should "serve get friends endpoint" in {
    FriendService.dao = new FriendDaoMock
    FriendService.cache = new CacheMock
    val promise = Promise[String]

    vertx.createHttpClient()
      .getNow(8080, "127.0.0.1", "/participant/1/friends",
        r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
        })

    promise.future.map(res => res should equal("[{\"id\":1,\"from\":\"/participant/1\",\"to\":\"/participant/2\"}]"))
  }
  
  "Friends Create" should "serve create friend endpoint" in {
    FriendService.dao = new FriendDaoMock
    FriendService.cache = new CacheMock
    val promise = Promise[String]
    
    vertx.createHttpClient().post(8080, "127.0.0.1", "/participant/1/friends")
      .handler(r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
      })
      .end("{\"from\":\"/participant/1\",\"to\":\"/participant/2\"}")

    promise.future.map(res => res should equal("{\"id\":null,\"from\":\"/participant/1\",\"to\":\"/participant/2\"}"))
  }
  
  "Inbound Get" should "serve get inbound endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient()
      .getNow(8080, "127.0.0.1", "/participant/2/inbound",
        r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
        })

    promise.future.map(res => res should equal("[{\"from\":\"/participant/1\",\"to\":\"/participant/2\",\"occurred\":null,\"subject\":\"test subject\",\"story\":\"test story\"}]"))
  }
  
  "Outbound Get" should "serve get outbound endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient()
      .getNow(8080, "127.0.0.1", "/participant/1/outbound",
        r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
        })

    promise.future.map(res => res should equal("[{\"from\":\"/participant/1\",\"occurred\":null,\"subject\":\"test subject\",\"story\":\"test story\"}]"))
  }
  
  "Outbound Create" should "serve create outbound endpoint" in {
    val promise = Promise[String]

    vertx.createHttpClient().post(8080, "127.0.0.1", "/participant/1/outbound")
      .handler(r => {
          r.exceptionHandler(promise.failure)
          r.bodyHandler(b => promise.success(b.toString))
      })
      .end("{\"from\":\"/participant/1\",\"occurred\":null,\"subject\":\"test subject\",\"story\":\"test story\"}")

    promise.future.map(res => res should equal("{\"from\":\"/participant/1\",\"occurred\":null,\"subject\":\"test subject\",\"story\":\"test story\"}"))
  }
  
  "Outbound Search" should "serve search outbound endpoint" in {
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
