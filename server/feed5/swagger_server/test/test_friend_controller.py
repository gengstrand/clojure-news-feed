# coding: utf-8

from __future__ import absolute_import

from swagger_server.models.friend import Friend
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestFriendController(BaseTestCase):
    """ FriendController integration test stubs """

    def test_add_friend(self):
        """
        Test case for add_friend

        create a new friendship
        """
        body = Friend(1, 1, 2)
        response = self.client.open('/participant/1/friends',
                                    method='POST',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

if __name__ == '__main__':
    import unittest
    unittest.main()
