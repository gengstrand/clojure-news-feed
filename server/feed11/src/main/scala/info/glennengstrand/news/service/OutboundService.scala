package info.glennengstrand.news.service

import io.vertx.lang.scala.ScalaLogger
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Outbound
import scala.concurrent._
import ExecutionContext.Implicits.global

object OutboundService {
  val LOGGER = ScalaLogger.getLogger("OutboundService")
  def create(p: Outbound, f: Try[Outbound] => Unit): Unit = {
    f(Success(p))
  }
  def get(id: Int, f: Try[Seq[Outbound]] => Unit): Unit = {
    f(Success(Seq(Outbound(Option("/participant/%d".format(id)), None, Option("test subject"), Option("test story")))))
  }
  def search(keywords: String, f: Try[Seq[String]] => Unit): Unit = {
    f(Success(Seq("/participant/1")))
  }
}