package info.glennengstrand.news.service

import io.vertx.lang.scala.ScalaLogger
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Friend
import scala.concurrent._
import ExecutionContext.Implicits.global

object FriendService {
  val LOGGER = ScalaLogger.getLogger("FriendService")
  def create(p: Friend, f: Try[Friend] => Unit): Unit = {
    f(Success(p))
  }
  def get(id: Int, f: Try[Seq[Friend]] => Unit): Unit = {
    f(Success(Seq(Friend(Option(1), Option("/participant/%d".format(id)), Option("/participant/2")))))
  }
}