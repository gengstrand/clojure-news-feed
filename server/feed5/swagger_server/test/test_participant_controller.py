# coding: utf-8

from __future__ import absolute_import

from swagger_server.models.participant import Participant
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestParticipantController(BaseTestCase):
    """ ParticipantController integration test stubs """

    def test_add_participant(self):
        """
        Test case for add_participant

        create a new participant
        """
        body = participant()
        response = self.client.open('//participant/new',
                                    method='POST',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_get_participant(self):
        """
        Test case for get_participant

        retrieve an individual participant
        """
        response = self.client.open('//participant/{id}'.format(id=789),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
