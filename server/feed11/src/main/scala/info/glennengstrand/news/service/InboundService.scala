package info.glennengstrand.news.service

import io.vertx.lang.scala.ScalaLogger
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Inbound
import scala.concurrent._
import ExecutionContext.Implicits.global

object InboundService {
  val LOGGER = ScalaLogger.getLogger("InboundService")
  def create(p: Inbound, f: Try[Inbound] => Unit): Unit = {
    f(Success(p))
  }
  def get(id: Int, f: Try[Seq[Inbound]] => Unit): Unit = {
    f(Success(Seq(Inbound(Option("/participant/1"), Option("/participant/%d".format(id)), None, Option("test subject"), Option("test story")))))
  }
}