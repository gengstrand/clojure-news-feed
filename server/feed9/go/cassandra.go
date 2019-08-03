package newsfeedserver

import (
        "os"
	"time"
	"github.com/gocql/gocql"
)

type AddCassandraWrapper interface {
        AddInbound(i Inbound)
        AddOutbound(o Outbound)
}

type CassandraWrapper struct {
	Session *gocql.Session
}

func connectCassandra() (*CassandraWrapper, error) {
	cluster := gocql.NewCluster(os.Getenv("NOSQL_HOST"))
	cluster.Keyspace = os.Getenv("NOSQL_KEYSPACE")
	cluster.Timeout = 10 * time.Second
	cluster.ConnectTimeout = 20 * time.Second
	cluster.Consistency = gocql.Any
	session, err := cluster.CreateSession()
	if err != nil {
	   return nil, err
	}
	retVal := CassandraWrapper{
	   Session: session,
	}
	return &retVal, nil
}