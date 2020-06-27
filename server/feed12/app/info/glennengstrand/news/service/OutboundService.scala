package info.glennengstrand.news.service

import javax.inject.{Inject, Provider}

import play.api.MarkerContext
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

import info.glennengstrand.news.model._
import info.glennengstrand.news.dao._
import info.glennengstrand.news.resource._

class OutboundService @Inject()(
    routerProvider: Provider[ParticipantRouter],
    outboundDao: OutboundDao,
    friendService: FriendService,
    inboundService: InboundService)(implicit ec: ExecutionContext) {
    private val logger = Logger(getClass)

  def create(id: Int, postInput: Outbound)(
      implicit mc: MarkerContext): Future[Outbound] = {
    val rv = outboundDao.create(id, postInput)
    friendService.lookup(id).map (friends => {
      friends.foreach(f => {
        val i = Inbound(
            from = postInput.from,
            to = f.to,
            occurred = postInput.occurred,
            subject = postInput.subject,
            story = postInput.story
        )
        inboundService.create(id, i)
      })
    })
    rv
  }

  def lookup(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Outbound]] = {
    outboundDao.get(id)
  }
  
  def search(keywords: String) (
      implicit mc: MarkerContext): Future[Seq[String]] = {
    outboundDao.search(keywords)
  }

}
