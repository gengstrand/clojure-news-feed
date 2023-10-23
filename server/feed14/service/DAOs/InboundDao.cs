using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using newsfeed.Models;
using Cassandra;

namespace newsfeed.DAOs;

public class InboundDao : CassandraDao, IInboundDao
{
    static readonly ILogger<InboundDao> logger = new LoggerFactory().CreateLogger<InboundDao>();
    private PreparedStatement? upsert;
    private PreparedStatement? select;

    private PreparedStatement Upsert {
        get {
            if (upsert == null) {
                lock (logger) {
                    if (upsert == null) {
                        upsert = CassandraSession.Prepare("insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?) using ttl");
                    }
                }
            }
            return upsert;
        }
    }

    private PreparedStatement Select {
        get {
            if (select == null) {
                lock (logger) {
                    if (select == null) {
                        select = CassandraSession.Prepare("select toTimestamp(Occurred) as Occurred, FromParticipantID, Subject, Story from Inbound where ParticipantID = ? order by Occurred desc");
                    }
                }
            }
            return select;
        }
    }

    public async Task<Outbound> CreateInboundAsync(string id, Inbound inbound)
    {
        var s = Upsert.Bind(int.Parse(inbound.To), int.Parse(id), inbound.Subject, inbound.Story);
        var rs = await CassandraSession.ExecuteAsync(s);
        return new Inbound(id, inbound.To, new DateOnly().ToString(), inbound.Subject, inbound.Story);
    }

    public async Task<IEnumerable<Inbound>> GetInboundAsync(string id)
    {
        var s = Select.Bind(int.Parse(id));
        var rs = await CassandraSession.ExecuteAsync(s);
        List<Inbound> rv = new();
	var rows = rs.GetRows().ToList();
        foreach (var row in rows) {
	    int fid = row.GetValue<int>("fromparticipantid");
	    DateOnly occurred = DateOnly.FromDateTime(row.GetValue<DateTime>("occurred"));
            rv.Add(new Inbound(fid.ToString(), id, occurred.ToString(), row.GetValue<string>("subject"), row.GetValue<string>("story")));
        }
        return rv;
    }
}