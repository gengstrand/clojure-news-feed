import connexion
from swagger_server.models.inbound import Inbound
from ..services.inbound_service import InboundService
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime

service = InboundService()

def get_inbound(id):
    """
    retrieve the inbound feed for an individual participant
    fetch inbound feed by id
    :param id: uniquely identifies the participant
    :type id: int

    :rtype: List[Inbound]
    """
    return service.fetch(id)
    
