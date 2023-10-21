using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class CacheDao : ICacheDao
{
    public async Task<string> GetValueAsync(string key)
    {
        throw new NotImplementedException();
    }

    public async Task<bool> SetValueAsync(string key, string value)
    {
        throw new NotImplementedException();
    }
}