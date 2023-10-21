using Microsoft.AspNetCore.Mvc;

namespace newsfeed.Interfaces;

public interface ISearchDao {
    Task<bool> IndexAsync(string id, string sender, string story);
    Task<IEnumerable<string>> SearchAsync(string keywords);
}