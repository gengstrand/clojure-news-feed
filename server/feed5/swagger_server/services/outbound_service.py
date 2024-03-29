import logging, time, json
from .friend_service import FriendService
from .inbound_service import InboundService
from ..daos.outbound_dao import Outbound as OutboundDAO
from ..models.outbound import Outbound
from ..models.friend import Friend
from ..models.inbound import Inbound
from ..util import extract_id, to_link
from .messaging_service import MessagingService
from .search_service import SearchService

friendService = FriendService()
inboundService = InboundService()
messages = MessagingService()
elastic = SearchService()

class OutboundService:

    def to_dict(self, o: dict) -> dict:
        retVal = {}
        retVal['from'] = to_link(o['from'])
        retVal['occurred'] = o['occurred']
        retVal['subject'] = o['subject']
        retVal['story'] = o['story']
        return retVal

    def fetch(self, id: int) -> Outbound:
        before = int(round(time.time() * 1000))
        retVal = list(map(self.to_dict, OutboundDAO(id).load()))
        after = int(round(time.time() * 1000))
        messages.log('outbound', 'get', after - before)
        return retVal

    def insert(self, outbound: Outbound) -> Outbound:
        before = int(round(time.time() * 1000))
        fid = extract_id(outbound._from)
        o = OutboundDAO(fid, outbound.subject, outbound.story)
        o.save()
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
        return list(map(to_link, elastic.search(keywords)))
