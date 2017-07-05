import logging
from .caching_service import CachingService
from ..daos.participant_dao import Participant as ParticipantDAO
from ..models.participant import Participant

class ParticipantService(CachingService):

    def key(self, id: int) -> str:
        return 'Participant::' + str(id)

    def to_dict(self, p: ParticipantDAO) -> dict:
        retVal = {}
        retVal['id'] = p.id()
        retVal['name'] = p.name()
        return retVal

    def to_participant(self, p: ParticipantDAO) -> Participant:
        return Participant(p.id(), p.name())
        
    def fetch(self, id: int) -> Participant:
        retVal = None
        cv = self.get(self.key(id))
        if cv is None:
            p = ParticipantDAO.query.get_or_404(id)
            retVal = self.to_participant(p)
            self.set(self.key(id), self.to_dict(p))
        else:
            retVal = Participant.from_dict(cv)
        return retVal

    def insert(self, participant: Participant) -> Participant:
        p = ParticipantDAO(participant.name)
        p.save()
        return self.to_participant(p)
