package newsfeedserver

import (
        "os"
	"log"
	"time"
	"github.com/gocql/gocql"
)

func CreateSession()(*gocql.Session) {
	cluster := gocql.NewCluster(os.Getenv("NOSQL_HOST"))
	cluster.Keyspace = os.Getenv("NOSQL_KEYSPACE")
	cluster.Timeout = 10 * time.Second
	cluster.ConnectTimeout = 20 * time.Second
	cluster.Consistency = gocql.Any
	retVal, err := cluster.CreateSession()
	if err != nil {
	   log.Println(err)
	}
	return retVal
}

var session = CreateSession()

type AddCassandraWrapper interface {
        AddInbound(i Inbound)
        AddOutbound(o Outbound)
}

type CassandraWrapper struct {
	Session *gocql.Session
}

func (cw CassandraWrapper) AddOutbound(o Outbound) {
	stmt := cw.Session.Query("insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?) using ttl 7776000", o.From, o.Subject, o.Story)
	stmt.Consistency(gocql.One)
	stmt.Exec()
}

func (cw CassandraWrapper) AddInbound(i Inbound) {
	stmt := cw.Session.Query("insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?) using ttl 7776000", i.To, i.From, i.Subject, i.Story)
	stmt.Consistency(gocql.Any)
	stmt.Exec()
}

func connectCassandra() (*CassandraWrapper) {
	retVal := CassandraWrapper{
	   Session: session,
	}
	return &retVal
}