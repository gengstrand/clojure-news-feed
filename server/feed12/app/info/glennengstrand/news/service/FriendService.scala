package info.glennengstrand.news.service

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

import info.glennengstrand.news.model._
import info.glennengstrand.news.dao._
import info.glennengstrand.news.resource._

class FriendService @Inject()(
    routerProvider: Provider[ParticipantRouter],
    friendDao: FriendDao)(implicit ec: ExecutionContext) {

  def create(id: Int, postInput: Friend)(
      implicit mc: MarkerContext): Future[Friend] = {
    friendDao.create(id, postInput)
  }

  def lookup(id: Int)(
      implicit mc: MarkerContext): Future[Seq[Friend]] = {
    friendDao.get(id)
  }

}
