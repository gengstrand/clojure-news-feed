import logging, time
from .caching_service import CachingService
from ..daos.participant_dao import Participant as ParticipantDAO
from ..models.participant import Participant
from .messaging_service import MessagingService

messages = MessagingService()

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
        before = int(round(time.time() * 1000))
        retVal = None
        cv = self.get(self.key(id))
        if cv is None:
            p = ParticipantDAO.query.get_or_404(id)
            retVal = self.to_participant(p)
            self.set(self.key(id), self.to_dict(p))
        else:
            retVal = Participant.from_dict(cv)
        after = int(round(time.time() * 1000))
        messages.log('participant', 'get', after - before)
        return retVal

    def insert(self, participant: Participant) -> Participant:
        before = int(round(time.time() * 1000))
        p = ParticipantDAO(participant.name)
        p.save()
        after = int(round(time.time() * 1000))
        messages.log('participant', 'post', after - before)
        return self.to_participant(p)
