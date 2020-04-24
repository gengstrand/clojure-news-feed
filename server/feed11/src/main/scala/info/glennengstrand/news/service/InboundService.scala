package info.glennengstrand.news.service

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Inbound
import scala.concurrent._
import ExecutionContext.Implicits.global

object InboundService extends NewsFeedService {
  def create(p: Inbound, f: Try[Inbound] => Unit): Unit = {
    f(Success(p))
  }
  def get(id: Int, f: Try[Seq[Inbound]] => Unit): Unit = {
    f(Success(Seq(Inbound(Option("/participant/1"), Option("/participant/%d".format(id)), None, Option("test subject"), Option("test story")))))
  }
}