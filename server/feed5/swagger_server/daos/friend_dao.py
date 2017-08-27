from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class Friend(db.Model):
    FriendsID = db.Column(db.Integer, primary_key=True)
    FromParticipantID = db.Column(db.Integer)
    ToParticipantID = db.Column(db.Integer)
    __tablename__ = 'Friends'

    @classmethod
    def fetch(cls, id):
        return Friend.query.filter_by(FromParticipantID=id)

    def __init__(self, _from, to):
        self.FromParticipantID = _from
        self.ToParticipantID = to

    def save(self):
        db.session.add(self)
        db.session.commit()

    def close(self):
        db.session.close()

    def __repr__(self):
        return '{"id":%d, "from":%d, "to":%d}' % (self.FriendsID, self.FromParticipantID, self.ToParticipantID)

    def id(self) -> int:
        return self.FriendsID

    def _from(self) -> int:
        return self.FromParticipantID

    def to(self) -> int:
        return self.ToParticipantID
