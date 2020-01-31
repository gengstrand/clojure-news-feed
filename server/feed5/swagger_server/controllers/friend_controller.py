import connexion
from swagger_server.models.friend import Friend
from ..services.friend_service import FriendService
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import to_link, deserialize_date, deserialize_datetime

service = FriendService()

def add_friend(id, body):
    """
    create a new friendship
    friends are those participants who receive news
    :param id: uniquely identifies the participant
    :type id: int
    :param body: friendship to be created
    :type body: dict | bytes

    :rtype: Friend
    """
    body["_from"] = to_link(id)
    return service.insert(Friend.from_dict(body))
    
def get_friend(id):
    """
    retrieve the list of friends for an individual participant
    fetch participant friends
    :param id: uniquely identifies the participant
    :type id: int

    :rtype: List[Friend]
    """
    return service.search(id)
    
