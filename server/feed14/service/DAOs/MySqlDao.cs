using MySql.Data.MySqlClient;

namespace newsfeed.DAOs;

public class MySqlDao {
    protected static readonly string ConnectionString = $"Server={Environment.GetEnvironmentVariable("MYSQL_HOST")};Database=feed;Uid=feed;Pwd=feed1234;";
}