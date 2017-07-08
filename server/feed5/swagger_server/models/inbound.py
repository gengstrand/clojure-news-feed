# coding: utf-8

from __future__ import absolute_import
from .base_model_ import Model
from datetime import date, datetime
from typing import List, Dict
from ..util import deserialize_model


class Inbound(Model):
    """
    NOTE: This class is auto generated by the swagger code generator program.
    Do not edit the class manually.
    """
    def __init__(self, _from: int=None, to: int=None, occurred: datetime=None, subject: str=None, story: str=None):
        """
        Inbound - a model defined in Swagger

        :param _from: The _from of this Inbound.
        :type _from: int
        :param to: The to of this Inbound.
        :type to: int
        :param occurred: The occurred of this Inbound.
        :type occurred: datetime
        :param subject: The subject of this Inbound.
        :type subject: str
        :param story: The story of this Inbound.
        :type story: str
        """
        self.swagger_types = {
            '_from': int,
            'to': int,
            'occurred': datetime,
            'subject': str,
            'story': str
        }

        self.attribute_map = {
            '_from': 'from',
            'to': 'to',
            'occurred': 'occurred',
            'subject': 'subject',
            'story': 'story'
        }

        self.__from = _from
        self._to = to
        self._occurred = occurred
        self._subject = subject
        self._story = story

    @classmethod
    def from_dict(cls, dikt) -> 'Inbound':
        """
        Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The inbound of this Inbound.
        :rtype: Inbound
        """
        return deserialize_model(dikt, cls)

    @property
    def _from(self) -> int:
        """
        Gets the _from of this Inbound.

        :return: The _from of this Inbound.
        :rtype: int
        """
        return self.__from

    @_from.setter
    def _from(self, _from: int):
        """
        Sets the _from of this Inbound.

        :param _from: The _from of this Inbound.
        :type _from: int
        """

        self.__from = _from

    @property
    def to(self) -> int:
        """
        Gets the to of this Inbound.

        :return: The to of this Inbound.
        :rtype: int
        """
        return self._to

    @to.setter
    def to(self, to: int):
        """
        Sets the to of this Inbound.

        :param to: The to of this Inbound.
        :type to: int
        """

        self._to = to

    @property
    def occurred(self) -> datetime:
        """
        Gets the occurred of this Inbound.

        :return: The occurred of this Inbound.
        :rtype: datetime
        """
        return self._occurred

    @occurred.setter
    def occurred(self, occurred: datetime):
        """
        Sets the occurred of this Inbound.

        :param occurred: The occurred of this Inbound.
        :type occurred: datetime
        """

        self._occurred = occurred

    @property
    def subject(self) -> str:
        """
        Gets the subject of this Inbound.

        :return: The subject of this Inbound.
        :rtype: str
        """
        return self._subject

    @subject.setter
    def subject(self, subject: str):
        """
        Sets the subject of this Inbound.

        :param subject: The subject of this Inbound.
        :type subject: str
        """

        self._subject = subject

    @property
    def story(self) -> str:
        """
        Gets the story of this Inbound.

        :return: The story of this Inbound.
        :rtype: str
        """
        return self._story

    @story.setter
    def story(self, story: str):
        """
        Sets the story of this Inbound.

        :param story: The story of this Inbound.
        :type story: str
        """

        self._story = story
