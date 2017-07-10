import logging, time
from ..daos.inbound_dao import Inbound as InboundDAO
from ..models.inbound import Inbound
from .messaging_service import MessagingService

messages = MessagingService()

class InboundService:

    def fetch(self, id: int) -> Inbound:
        before = int(round(time.time() * 1000))
        retVal = InboundDAO(id).load()
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
