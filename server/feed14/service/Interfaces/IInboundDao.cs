using Microsoft.AspNetCore.Mvc;
using newsfeed.Models;

namespace newsfeed.Interfaces;

public interface IInboundDao {
    Task<IEnumerable<Inbound>> GetInboundAsync(string id);
    Task<Outbound> CreateInboundAsync(string id, Inbound inbound);
}