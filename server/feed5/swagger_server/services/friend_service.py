import logging, time
from .caching_service import CachingService
from ..daos.friend_dao import Friend as FriendDAO
from ..models.friend import Friend
from .messaging_service import MessagingService

messages = MessagingService()

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
        
    def insert(self, friend: Friend) -> Friend:
        before = int(round(time.time() * 1000))
        f = FriendDAO(friend._from, friend.to)
        f.save()
        self.remove(self.key(friend._from))
        after = int(round(time.time() * 1000))
        messages.log('friends', 'post', after - before)
        return self.to_friend(f)

    def search(self, fromFriend: int):
        before = int(round(time.time() * 1000))
        k = self.key(fromFriend)
        retVal = self.get(k)
        if retVal is None:
            retVal = list(map(self.to_dict, FriendDAO.query.filter_by(FromParticipantID=fromFriend)))
            self.set(k, retVal)
        after = int(round(time.time() * 1000))
        messages.log('friends', 'get', after - before)
        return retVal
