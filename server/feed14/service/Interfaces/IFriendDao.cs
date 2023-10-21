using Microsoft.AspNetCore.Mvc;
using newsfeed.Models;

namespace newsfeed.Interfaces;

public interface IFriendDao {
    Task<IEnumerable<Friend>> GetFriendsAsync(string id);
    Task<Friend> CreateFriendAsync(string id, Friend friend);    
}