using MySql.Data.MySqlClient;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class ParticipantDao : MySqlDao, IParticipantDao
{
    static readonly ILogger<ParticipantDao> logger = new LoggerFactory().CreateLogger<ParticipantDao>();

    public async Task<Participant> CreateParticipantAsync(Participant participant)
    {
        using MySqlConnection connection = new MySqlConnection(ConnectionString);
        await connection.OpenAsync();
        MySqlDataReader reader = await MySqlHelper.ExecuteReaderAsync(connection, "call UpsertParticipant(@moniker);", CancellationToken.None, new MySqlParameter[] { new MySqlParameter("@moniker", MySqlDbType.String) { Value = participant.Name } });
        Participant rv = participant;
        while (reader.Read())
        {
            rv = new Participant(reader.GetString(0), participant.Name);
        }
        reader.Close();
        return rv;
    }

    public async Task<Participant?> GetParticipantAsync(string id)
    {
        using MySqlConnection connection = new MySqlConnection(ConnectionString);
        await connection.OpenAsync();
        MySqlDataReader reader = await MySqlHelper.ExecuteReaderAsync(connection, "call FetchParticipant(@id);", CancellationToken.None, new MySqlParameter[] { new MySqlParameter("@id", MySqlDbType.Int32) { Value = int.Parse(id) } }); 
        Participant? rv = null;
        while (reader.Read())
        {
            rv = new Participant(id, reader.GetString(0));
        }
        reader.Close();
        return rv;
    }
}