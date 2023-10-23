using MySql.Data.MySqlClient;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class ParticipantDao : MySqlDao, IParticipantDao
{
    static readonly ILogger<ParticipantDao> logger = new LoggerFactory().CreateLogger<ParticipantDao>();

    public async Task<Participant> CreateParticipantAsync(Participant participant)
    {
        MySqlDataReader reader = await MySqlHelper.ExecuteReaderAsync(ConnectionString, "call UpsertParticipant(@moniker);", CancellationToken.None, new MySqlParameter[] { new MySqlParameter("@moniker", MySqlDbType.String) { Value = participant.Name } }); 
        while (reader.Read())
        {
            return new Participant(reader.GetString(0), participant.Name);
        }
        return participant;
    }

    public async Task<Participant?> GetParticipantAsync(string id)
    {
        MySqlDataReader reader = await MySqlHelper.ExecuteReaderAsync(ConnectionString, "call FetchParticipant(@id);", CancellationToken.None, new MySqlParameter[] { new MySqlParameter("@id", MySqlDbType.Int32) { Value = int.Parse(id) } }); 
        while (reader.Read())
        {
            return new Participant(id, reader.GetString(0));
        }
        return null;
    }
}