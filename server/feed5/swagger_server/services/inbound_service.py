import logging, time
from ..daos.inbound_dao import Inbound as InboundDAO
from ..models.inbound import Inbound
from .messaging_service import MessagingService
from ..util import to_link

messages = MessagingService()

class InboundService:

    def to_dict(self, i: dict) -> dict:
        retVal = {}
        retVal['from'] = to_link(i['from'])
        retVal['to'] = to_link(i['to'])
        retVal['occurred'] = i['occurred']
        retVal['subject'] = i['subject']
        retVal['story'] = i['story']
        return retVal

    def fetch(self, id: int) -> Inbound:
        before = int(round(time.time() * 1000))
        retVal = list(map(self.to_dict, InboundDAO(id).load()))
        after = int(round(time.time() * 1000))
        messages.log('inbound', 'get', after - before)
        return retVal

    def insert(self, inbound: Inbound) -> Inbound:
        before = int(round(time.time() * 1000))
        i = InboundDAO(inbound.to, inbound._from, inbound.subject, inbound.story)
        i.save()
        after = int(round(time.time() * 1000))
        messages.log('inbound', 'post', after - before)
        return inbound
