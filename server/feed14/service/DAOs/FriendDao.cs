using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class FriendDao : IFriendDao
{
    static readonly ILogger<FriendDao> logger = new LoggerFactory().CreateLogger<FriendDao>();

    public async Task<Friend> CreateFriendAsync(string id, Friend friend)
    {
        throw new NotImplementedException();
    }

    public async Task<IEnumerable<Friend>> GetFriendsAsync(string id)
    {
        throw new NotImplementedException();
    }
}