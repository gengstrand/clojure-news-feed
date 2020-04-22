package info.glennengstrand.news.service

import io.vertx.lang.scala.ScalaLogger
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import info.glennengstrand.news.model.Participant
import scala.concurrent._
import ExecutionContext.Implicits.global

object ParticipantService {
  val LOGGER = ScalaLogger.getLogger("ParticipantService")
  def create(p: Participant, f: Try[Participant] => Unit): Unit = {
    f(Success(p))
  }
  def get(id: Int, f: Try[Participant] => Unit): Unit = {
    f(Success(Participant(Option(id), Option("test"), Option("/participant/%d".format(id)))))
  }
}