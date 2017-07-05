# coding: utf-8

from __future__ import absolute_import

from swagger_server.models.inbound import Inbound
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestInboundController(BaseTestCase):
    """ InboundController integration test stubs """

    def test_get_inbound(self):
        """
        Test case for get_inbound

        retrieve the inbound feed for an individual participant
        """
        response = self.client.open('//inbound/{id}'.format(id=789),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
