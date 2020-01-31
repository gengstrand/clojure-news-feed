from ..models.outbound import Outbound
from .cassandra_dao import CassandraDAO

class Outbound(CassandraDAO):
    _from = None
    subject = None
    story = None
    insert = 'insert_outbound'
    query = 'select_outbound'

    def __init__(self, _from: int = None, subject: str = None, story: str = None):
        self._from = _from
        self.subject = subject
        self.story = story
        self.prepare(self.insert, 'insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)')
        self.prepare(self.query, 'select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc')

    def save(self):
        self.execute(self.insert, [ self._from, self.subject, self.story ])

    def makeOutbound(self, dikt):
        retVal = {}
        retVal['occurred'] = dikt['occurred'].strftime('%Y-%m-%d')
        retVal['from'] = self._from
        retVal['subject'] = dikt['subject']
        retVal['story'] = dikt['story']
        return retVal

    def load(self):
        return self.execute(self.query, [ self._from ], self.makeOutbound)

    def __repr__(self):
        return '{"from":%d, "subject":"%s", "story":"%s"}' % (self._from, self.subject, self.story)

    def _from(self) -> int:
        return self._from

    def subject(self) -> str:
        return self.subject

    def story(self) -> str:
        return self.story

