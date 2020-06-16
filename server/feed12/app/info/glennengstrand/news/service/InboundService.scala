package info.glennengstrand.news.service

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

import info.glennengstrand.news.model._
import info.glennengstrand.news.dao._
import info.glennengstrand.news.resource._

class InboundService @Inject()(
    routerProvider: Provider[ParticipantRouter],
    inboundDao: InboundDao)(implicit ec: ExecutionContext) {

  def create(postInput: Inbound)(
      implicit mc: MarkerContext): Future[Inbound] = {
    inboundDao.create(postInput)
  }

  def lookup(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Inbound]] = {
    inboundDao.get(id)
  }

}
