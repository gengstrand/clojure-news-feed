using MySql.Data.MySqlClient;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class FriendDao : MySqlDao, IFriendDao
{
    static readonly ILogger<FriendDao> logger = new LoggerFactory().CreateLogger<FriendDao>();

    public async Task<Friend> CreateFriendAsync(string id, Friend friend)
    {
        var otherId = id == friend.From ? friend.To : friend.From;
        MySqlDataReader reader = await MySqlHelper.ExecuteReaderAsync(ConnectionString, "call UpsertFriends(@from, @to);", CancellationToken.None, new MySqlParameter[] { new("@from", MySqlDbType.Int32) { Value = int.Parse(id) }, new("@to", MySqlDbType.Int32) { Value = int.Parse(otherId) } }); 
        while (reader.Read())
        {
            return new Friend(reader.GetString(0), id, otherId);
        }
        return friend;
    }

    public async Task<IEnumerable<Friend>> GetFriendsAsync(string id)
    {
        MySqlDataReader reader = await MySqlHelper.ExecuteReaderAsync(ConnectionString, "call FetchFriends(@id);", CancellationToken.None, new MySqlParameter[] { new MySqlParameter("@id", MySqlDbType.Int32) { Value = int.Parse(id) } }); 
        List<Friend> rv = new();
        while (reader.Read())
        {
            rv.Add(new Friend(reader.GetString(0), id, reader.GetString(1)));
        }
        return rv;
    }
}