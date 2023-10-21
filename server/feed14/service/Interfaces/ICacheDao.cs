namespace newsfeed.Interfaces;

public interface ICacheDao {
    Task<string?> GetValueAsync(string key);
    Task<bool> SetValueAsync(string key, string value);
}