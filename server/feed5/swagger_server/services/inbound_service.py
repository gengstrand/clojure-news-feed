import logging
from ..daos.inbound_dao import Inbound as InboundDAO
from ..models.inbound import Inbound

class InboundService:

    def fetch(self, id: int) -> Inbound:
        return InboundDAO(id).load()

    def insert(self, inbound: Inbound) -> Inbound:
        i = InboundDAO(inbound.to, inbound._from, inbound.subject, inbound.story)
        i.save()
        return inbound
