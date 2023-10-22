using Cassandra;

namespace newsfeed.DAOs;

public class CassandraDao {
    private static Cluster? cluster;
    private static Cassandra.ISession? session;

    private static readonly string keyspace = Environment.GetEnvironmentVariable("NOSQL_KEYSPACE") ?? "activity";

    protected Cassandra.ISession CassandraSession {
        get {
            if (session == null) {
                lock(keyspace) {
                    if (session == null) {
                        cluster = Cluster.Builder().AddContactPoint(Environment.GetEnvironmentVariable("NOSQL_HOST")).Build();
                        session = cluster.Connect(keyspace);
                    }
                }
            }
            return session;
        }
    }
}