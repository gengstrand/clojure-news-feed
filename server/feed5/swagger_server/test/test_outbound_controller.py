# coding: utf-8

from __future__ import absolute_import

from swagger_server.models.outbound import Outbound
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestOutboundController(BaseTestCase):
    """ OutboundController integration test stubs """

    def test_add_outbound(self):
        """
        Test case for add_outbound

        create a participant news item
        """
        body = outbound()
        response = self.client.open('//outbound/new',
                                    method='POST',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_get_outbound(self):
        """
        Test case for get_outbound

        retrieve the news posted by an individual participant
        """
        response = self.client.open('//outbound/{id}'.format(id=789),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_search_outbound(self):
        """
        Test case for search_outbound

        create a participant news item
        """
        query_string = [('keywords', 'keywords_example')]
        response = self.client.open('//outbound/search',
                                    method='POST',
                                    query_string=query_string)
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
