import connexion
from swagger_server.models.outbound import Outbound
from ..services.outbound_service import OutboundService
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime

service = OutboundService()

def add_outbound(body):
    """
    create a participant news item
    socially broadcast participant news
    :param body: outbound news item
    :type body: dict | bytes

    :rtype: Outbound
    """
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
    create a participant news item
    keyword search of participant news
    :param keywords: keywords to search for
    :type keywords: str

    :rtype: List[int]
    """
    return service.search(keywords)
    
