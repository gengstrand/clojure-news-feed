package info.glennengstrand.news.resource

import io.vertx.lang.scala.ScalaLogger
import io.vertx.scala.core.{Vertx, DeploymentOptions}
import scala.util.Random

object Topics extends Enumeration {
  class TopicValue(val i: Int, val name: String, val instances: Int) extends Val(i: Int, name: String)

  val CreateParticipant = new TopicValue(0, "CreateParticipant", 5) 
  val GetParticipant = new TopicValue(1, "GetParticipant", 3)
  val CreateFriend = new TopicValue(2, "CreateFriend", 5) 
  val GetFriend = new TopicValue(3, "GetFriend", 5)
  val CreateInbound = new TopicValue(4, "CreateInbound", 5) 
  val GetInbound = new TopicValue(5, "GetInbound", 3)
  val CreateOutbound = new TopicValue(6, "CreateOutbound", 5) 
  val GetOutbound = new TopicValue(7, "GetOutbound", 3)
  val SearchOutbound = new TopicValue(8, "SearchOutbound", 5)
}

trait NewsFeedResource {
  protected val LOGGER = ScalaLogger.getLogger("HttpVerticle")
  private val r = Random
  private val depOpts = new io.vertx.core.DeploymentOptions()

  protected def key(ns: String): String = {
    ns.concat(r.nextString(20))
  }
  protected def publish[E <: AnyRef](topic: String, e: E, vertx: Vertx): Unit = {
    try {
      vertx
        .eventBus()
        .send(topic, e)
    } catch {
      case ex: Exception => {
        LOGGER.error("cannot publish event: ", ex)
      }
    }
  }
  protected def genDeployOptions(t: Topics.TopicValue): DeploymentOptions = {
    new DeploymentOptions(depOpts).setInstances(t.instances)
  }
}