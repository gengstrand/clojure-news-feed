import logging
from .caching_service import CachingService
from ..daos.friend_dao import Friend as FriendDAO
from ..models.friend import Friend

class FriendService(CachingService):

    def key(self, id: int) -> str:
        return 'Friend::' + str(id)

    def to_dict(self, f: FriendDAO) -> dict:
        retVal = {}
        retVal['id'] = f.id()
        retVal['from'] = f._from()
        retVal['to'] = f.to()
        return retVal

    def to_friend(self, f: FriendDAO) -> Friend:
        return Friend(f.id(), f._from(), f.to())
        
    def fetch(self, id: int) -> Friend:
        retVal = None
        cv = self.get(self.key(id))
        if cv is None:
            f = FriendDAO.query.get_or_404(id)
            retVal = self.to_friend(f)
            self.set(self.key(id), self.to_dict(f))
        else:
            retVal = Friend.from_dict(cv)
        return retVal

    def insert(self, friend: Friend) -> Friend:
        f = FriendDAO(friend._from, friend.to)
        f.save()
        return self.to_friend(f)

    def friends(self, fromFriend: int):
        return map(self.to_friend, FriendDAO.query.filter_by(FromParticipantID=fromFriend))
