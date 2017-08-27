from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class Participant(db.Model):
    ParticipantID = db.Column(db.Integer, primary_key=True)
    Moniker = db.Column(db.String(50))
    __tablename__ = 'Participant'

    @classmethod
    def fetch(cls, id):
        return Participant.query.get_or_404(id)

    def __init__(self, name):
        self.Moniker = name

    def save(self):
        db.session.add(self)
        db.session.commit()

    def close(self):
        db.session.close()

    def __repr__(self):
        return '{"id":%d, "name":"%s"}' % (self.ParticipantID, self.Moniker)

    def id(self) -> int:
        return self.ParticipantID

    def name(self) -> str:
        return self.Moniker
