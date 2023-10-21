using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;

namespace newsfeed.Services;

public class OutboundService : IOutboundService
{
    static readonly ILogger<OutboundService> logger = new LoggerFactory().CreateLogger<OutboundService>();
    private readonly ISearchDao _searchDao;

    public OutboundService(ISearchDao searchDao) {
        _searchDao = searchDao;
    }

    public async Task<IEnumerable<string>> Search(string keywords)
    {
        return await _searchDao.SearchAsync(keywords);
    }
}