package info.glennengstrand.news.service

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

import info.glennengstrand.news.model._
import info.glennengstrand.news.dao._
import info.glennengstrand.news.resource._

class ParticipantService @Inject()(
    routerProvider: Provider[ParticipantRouter],
    participantDao: ParticipantDao)(implicit ec: ExecutionContext) {

  def create(postInput: Participant)(
      implicit mc: MarkerContext): Future[Participant] = {
    participantDao.create(postInput)
  }

  def lookup(id: Int)(
      implicit mc: MarkerContext): Future[Participant] = {
    participantDao.get(id)
  }

}
