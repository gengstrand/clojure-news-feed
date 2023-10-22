using newsfeed.Interfaces;
using StackExchange.Redis;

namespace newsfeed.DAOs;

public class CacheDao : ICacheDao
{
    private ConnectionMultiplexer? redis;

    private ConnectionMultiplexer Redis {
        get {
            if (redis == null) {
                lock(this) {
                    if (redis == null) {
                        redis = ConnectionMultiplexer.Connect($"{Environment.GetEnvironmentVariable("REDIS_HOST")}:6379");
                    }
                }
            }
            return redis;
        }
    }

    public async Task<string?> GetValueAsync(string key)
    {
        IDatabase db = Redis.GetDatabase();
        return await db.StringGetAsync(key);
    }

    public async Task<bool> SetValueAsync(string key, string value)
    {
        IDatabase db = Redis.GetDatabase();
        return await db.StringSetAsync(key, value);
    }
}