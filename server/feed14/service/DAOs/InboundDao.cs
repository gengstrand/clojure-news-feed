using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class InboundDao : IInboundDao
{
    static readonly ILogger<InboundDao> logger = new LoggerFactory().CreateLogger<InboundDao>();

    public async Task<Outbound> CreateInboundAsync(string id, Inbound inbound)
    {
        throw new NotImplementedException();
    }

    public async Task<IEnumerable<Inbound>> GetInboundAsync(string id)
    {
        throw new NotImplementedException();
    }
}