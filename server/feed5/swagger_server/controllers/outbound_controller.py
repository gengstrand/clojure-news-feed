import connexion
from swagger_server.models.outbound import Outbound
from ..services.outbound_service import OutboundService
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import to_link, deserialize_date, deserialize_datetime

service = OutboundService()

def add_outbound(id, body):
    """
    create a participant news item
    socially broadcast participant news
    :param id: uniquely identifies the participant
    :type id: int
    :param body: outbound news item
    :type body: dict | bytes

    :rtype: Outbound
    """
    body["_from"] = to_link(id)
    return service.insert(Outbound.from_dict(body))
    
def get_outbound(id):
    """
    retrieve the news posted by an individual participant
    fetch a participant news
    :param id: uniquely identifies the participant
    :type id: int

    :rtype: List[Outbound]
    """
    return service.fetch(id)
    
def search_outbound(keywords):
    """
    search outbound feed items for terms
    keyword search of participant news
    :param keywords: keywords to search for
    :type keywords: str

    :rtype: List[str]
    """
    return service.search(keywords)
    
