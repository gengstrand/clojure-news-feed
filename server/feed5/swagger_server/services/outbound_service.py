import logging, time, json
from .friend_service import FriendService
from .inbound_service import InboundService
from ..daos.outbound_dao import Outbound as OutboundDAO
from ..models.outbound import Outbound
from ..models.friend import Friend
from ..models.inbound import Inbound
from ..util import extract_id
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
        fid = extract_id(outbound._from)
        friends = list(map(Friend.from_dict, friendService.search(fid)))
        for friend in friends:
            f1 = extract_id(friend._from)
            f2 = extract_id(friend.to)
            if f2 == fid:
                f3 = f1
                f1 = f2
                f2 = f3
            inboundService.insert(Inbound(f1, f2, outbound.occurred, outbound.subject, outbound.story)) 
        elastic.create(fid, outbound.story)
        after = int(round(time.time() * 1000))
        messages.log('outbound', 'post', after - before)
        return outbound

    def search(self, keywords: str):
        return list(elastic.search(keywords))
