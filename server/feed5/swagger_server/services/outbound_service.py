import logging, time, json
from .friend_service import FriendService
from .inbound_service import InboundService
from ..daos.outbound_dao import Outbound as OutboundDAO
from ..models.outbound import Outbound
from ..models.friend import Friend
from ..models.inbound import Inbound
from .messaging_service import MessagingService
from .search_service import SearchService

friendService = FriendService()
inboundService = InboundService()
messages = MessagingService()
elastic = SearchService()

class OutboundService:

    def fetch(self, id: int) -> Outbound:
        before = int(round(time.time() * 1000))
        retVal = OutboundDAO(id).load()
        after = int(round(time.time() * 1000))
        messages.log('outbound', 'get', after - before)
        return retVal

    def insert(self, outbound: Outbound) -> Outbound:
        before = int(round(time.time() * 1000))
        o = OutboundDAO(outbound._from, outbound.subject, outbound.story)
        o.save()
        for friend in friendService.search(outbound._from):
            inboundService.insert(Inbound(friend._from, friend.to, outbound.occurred, outbound.subject, outbound.story)) 
        elastic.create(outbound._from, outbound.story)
        after = int(round(time.time() * 1000))
        messages.log('outbound', 'post', after - before)
        return outbound

    def search(self, keywords: str):
        return list(elastic.search(keywords))
