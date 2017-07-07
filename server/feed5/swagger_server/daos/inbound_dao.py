from ..models.inbound import Inbound
from .cassandra_dao import CassandraDAO

class Inbound(CassandraDAO):
    _from = None
    to = None
    subject = None
    story = None
    insert = 'insert_inbound'
    query = 'select_inbound'

    def __init__(self, to: str, _from: str = None, subject: str = None, story: str = None):
        self._from = _from
        self.to = to
        self.subject = subject
        self.story = story
        self.prepare(self.insert, 'insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?)')
        self.prepare(self.query, 'select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc')

    def save(self):
        self.execute(self.insert, [ self.to, self._from, self.subject, self.story ])

    def load(self):
        return self.execute(self.query, [ self.to ], Inbound.from_dict)

    def __repr__(self):
        return '{"from":%d, "to":%d, "subject":"%s", "story":"%s"}' % (self._from, self.to, self.subject, self.story)

    def _from(self) -> int:
        return self._from

    def to(self) -> int:
        return self.to

    def subject(self) -> str:
        return self.subject

    def story(self) -> str:
        return self.story
