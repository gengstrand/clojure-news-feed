import connexion
from swagger_server.models.participant import Participant
from ..services.participant_service import ParticipantService
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime

service = ParticipantService()

def add_participant(body):
    """
    create a new participant
    a participant is someone who can post news to friends
    :param body: participant to be created
    :type body: dict | bytes

    :rtype: Participant
    """
    return service.insert(Participant.from_dict(body))
    
def get_participant(id):
    """
    retrieve an individual participant
    fetch a participant by id
    :param id: uniquely identifies the participant
    :type id: int

    :rtype: Participant
    """
    return service.fetch(id)
    
