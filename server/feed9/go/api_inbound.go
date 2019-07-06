
package newsfeedserver

import (
        "os"
        "fmt"
	"time"
	"strconv"
	"net/http"
	"encoding/json"
	"github.com/gorilla/mux"
	"github.com/gocql/gocql"
)

func AddInbound(i Inbound, session *gocql.Session) {
	stmt := session.Query("insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?) using ttl 7776000", i.To, i.From, i.Subject, i.Story)
	stmt.Consistency(gocql.One)
	stmt.Exec()
}

func GetInbound(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	cluster := gocql.NewCluster(os.Getenv("NOSQL_HOST"))
	cluster.Keyspace = os.Getenv("NOSQL_KEYSPACE")
	session, _ := cluster.CreateSession()
	defer session.Close()

	vars := mux.Vars(r)
	i, err := strconv.ParseInt(vars["id"], 0, 16)
	if err != nil {
	    fmt.Fprintf(w, "id is not an integer: %s", err)
	    w.WriteHeader(http.StatusInternalServerError)
	    return
	}
	stmt := session.Query("select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc", vars["id"])
	stmt.Consistency(gocql.One)
	iter := stmt.Iter()
	defer iter.Close()
	var occurred time.Time
	var from int64
	var subject string
	var story string
	var results []Inbound
	for iter.Scan(&occurred, &from, &subject, &story) {
	    inb := Inbound {
	      From: from,
	      To: i,
	      Occurred: occurred,
	      Subject: subject,
	      Story: story,
	    }
	    results = append(results, inb)
	}
	resultb, err := json.Marshal(results)
	if err != nil {
	    fmt.Fprintf(w, "cannot marshal data: %s", err)
	    w.WriteHeader(http.StatusInternalServerError)
	    return
	}
	fmt.Fprint(w, string(resultb))
	w.WriteHeader(http.StatusOK)
}
