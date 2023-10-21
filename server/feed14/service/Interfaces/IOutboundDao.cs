using Microsoft.AspNetCore.Mvc;
using newsfeed.Models;

namespace newsfeed.Interfaces;

public interface IOutboundDao {
    Task<IEnumerable<Outbound>> GetOutboundAsync(string id);
    Task<Outbound> CreateOutboundAsync(string id, Outbound outbound);
}