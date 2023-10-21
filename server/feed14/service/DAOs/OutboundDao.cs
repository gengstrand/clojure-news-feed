using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class OutboundDao : IOutboundDao
{
    static readonly ILogger<OutboundDao> logger = new LoggerFactory().CreateLogger<OutboundDao>();

    public async Task<Outbound> CreateOutboundAsync(string id, Outbound outbound)
    {
        throw new NotImplementedException();
    }

    public async Task<IEnumerable<Outbound>> GetOutboundAsync(string id)
    {
        throw new NotImplementedException();
    }
}