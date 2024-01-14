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
        using MySqlConnection connection = new MySqlConnection(ConnectionString);
        await connection.OpenAsync();
        MySqlDataReader reader = await MySqlHelper.ExecuteReaderAsync(connection, "call UpsertFriends(@from, @to);", CancellationToken.None, new MySqlParameter[] { new("@from", MySqlDbType.Int32) { Value = int.Parse(id) }, new("@to", MySqlDbType.Int32) { Value = int.Parse(otherId) } }); 
        Friend rv = friend;
        while (reader.Read())
        {
            rv = new Friend(reader.GetString(0), id, otherId);
        }
        reader.Close();
        return rv;
    }

    public async Task<IEnumerable<Friend>> GetFriendsAsync(string id)
    {
        using MySqlConnection connection = new MySqlConnection(ConnectionString);
        await connection.OpenAsync();
        MySqlDataReader reader = await MySqlHelper.ExecuteReaderAsync(connection, "call FetchFriends(@id);", CancellationToken.None, new MySqlParameter[] { new MySqlParameter("@id", MySqlDbType.Int32) { Value = int.Parse(id) } }); 
        List<Friend> rv = new();
        while (reader.Read())
        {
            rv.Add(new Friend(reader.GetString(0), id, reader.GetString(1)));
        }
        reader.Close();
        return rv;
    }
}