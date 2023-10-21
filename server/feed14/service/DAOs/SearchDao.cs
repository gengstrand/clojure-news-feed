using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class SearchDao : ISearchDao
{
    static readonly ILogger<SearchDao> logger = new LoggerFactory().CreateLogger<SearchDao>();

    public async Task<bool> IndexAsync(string id, string sender, string story)
    {
        throw new NotImplementedException();
    }

    public async Task<IEnumerable<string>> SearchAsync(string keywords)
    {
        throw new NotImplementedException();
    }
}