using newsfeed.Interfaces;
using newsfeed.Models;
using Cassandra;

namespace newsfeed.DAOs;

public class OutboundDao : CassandraDao, IOutboundDao
{
    static readonly ILogger<OutboundDao> logger = new LoggerFactory().CreateLogger<OutboundDao>();

    private PreparedStatement? upsert;
    private PreparedStatement? select;

    private PreparedStatement Upsert {
        get {
            if (upsert == null) {
                lock (logger) {
                    if (upsert == null) {
                        upsert = CassandraSession.Prepare("insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?) using ttl 7776000");
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
                        select = CassandraSession.Prepare("select toTimestamp(Occurred) as Occurred, Subject, Story from Outbound where participantID = ? order by Occurred desc");
                    }
                }
            }
            return select;
        }
    }

    public async Task<Outbound> CreateOutboundAsync(string id, Outbound outbound)
    {
        var s = Upsert.Bind(int.Parse(id), outbound.Subject, outbound.Story);
        var rs = await CassandraSession.ExecuteAsync(s);
        return new Outbound(id, new DateOnly().ToString(), outbound.Subject, outbound.Story);
    }

    public async Task<IEnumerable<Outbound>> GetOutboundAsync(string id)
    {
        var s = Select.Bind(int.Parse(id));
        var rs = await CassandraSession.ExecuteAsync(s);
        List<Outbound> rv = new();
	var rows = rs.GetRows().ToList();
        foreach (var row in rows) {
	    DateOnly occurred = DateOnly.FromDateTime(row.GetValue<DateTime>("occurred"));
            rv.Add(new Outbound(id, occurred.ToString(), row.GetValue<string>("subject"), row.GetValue<string>("story")));
        }
        return rv;
    }
}