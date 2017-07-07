import logging
from .friend_service import FriendService
from .inbound_service import InboundService
from ..daos.outbound_dao import Outbound as OutboundDAO
from ..models.outbound import Outbound
from ..models.friend import Friend
from ..models.inbound import Inbound

friendService = FriendService()
inboundService = InboundService()

class OutboundService:

    def fetch(self, id: int) -> Outbound:
        return OutboundDAO(id).load()

    def insert(self, outbound: Outbound) -> Outbound:
        o = OutboundDAO(outbound._from, outbound.subject, outbound.story)
        o.save()
        for friend in friendService.friends(outbound._from):
            inboundService.insert(Inbound(friend._from, friend.to, outbound.occurred, outbound.subject, outbound.story)) 
        return outbound
