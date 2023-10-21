using Microsoft.AspNetCore.Mvc;

namespace newsfeed.Interfaces;

public interface IOutboundService {
    Task<IEnumerable<string>> Search(string keywords);
}
