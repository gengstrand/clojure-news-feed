from flask_testing import TestCase
from flask_sqlalchemy import SQLAlchemy
from ..encoder import JSONEncoder
import connexion
import logging
import unittest
from unittest.mock import MagicMock
from unittest import mock
from ..models.inbound import Inbound
from ..models.outbound import Outbound
from ..services.messaging_service import MessagingService
from ..services.caching_service import CachingService
from ..services.search_service import SearchService
from ..daos.participant_dao import Participant
from ..daos.friend_dao import Friend
from ..daos.inbound_dao import Inbound as InboundDAO
from ..daos.outbound_dao import Outbound as OutboundDAO

def mock_log(entity: str, operation: str, duration: int):
    pass

def mock_remove(key: str):
    pass

def mock_get(key: str):
    return None

def mock_set(key: str, value: dict):
    pass

def mock_inbound_load():
    return Inbound(1, 2, None, 'test subject', 'test story')

def mock_outbound_load():
    return Outbound(1, None, 'test subject', 'test story')

def mock_outbound_save():
    pass

def mock_search(term):
    return [1]

def mock_create(sender: int, story: str):
    pass

MessagingService.log = MagicMock(side_effect=mock_log)
CachingService.remove = MagicMock(side_effect=mock_remove)
CachingService.get = MagicMock(side_effect=mock_get)
CachingService.set = MagicMock(side_effect=mock_set)
InboundDAO.load = MagicMock(side_effect=mock_inbound_load)
OutboundDAO.load = MagicMock(side_effect=mock_outbound_load)
OutboundDAO.save = MagicMock(side_effect=mock_outbound_save)
SearchService.search = MagicMock(side_effect=mock_search)
SearchService.create = MagicMock(side_effect=mock_create)

class BaseTestCase(TestCase):

    def create_app(self):
        logging.getLogger('connexion.operation').setLevel('ERROR')
        app = connexion.App(__name__, specification_dir='../swagger/')
        app.add_api('swagger.yaml')
        app.app.json_encoder = JSONEncoder
        app.app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/feed5.db'
        db = SQLAlchemy(app.app)
        db.init_app(app.app)
        with app.app.app_context():
            db.create_all()
        return app.app
