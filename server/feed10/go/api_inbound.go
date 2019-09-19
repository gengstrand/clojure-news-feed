package newsfeedserver

import (
	"encoding/json"
	"net/http"
	"strconv"
	"time"

	"github.com/gocql/gocql"
	"github.com/gorilla/mux"
)

func AddInbound(i Inbound, session *gocql.Session) {
	stmt := session.Query("insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?) using ttl 7776000", i.To, i.From, i.Subject, i.Story)
	stmt.Consistency(gocql.Any)
	stmt.Exec()
}

func GetInbound(session *gocql.Session) http.Handler {

	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		i, err := strconv.ParseInt(vars["id"], 0, 64)
		if err != nil {
			LogError(w, err, "id is not an integer: %s", http.StatusInternalServerError)
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
			inb := Inbound{
				From:     from,
				To:       i,
				Occurred: occurred,
				Subject:  subject,
				Story:    story,
			}
			results = append(results, inb)
		}

		// We use an encoder since we want to write the result to the ResultWriter
		// anyway. Since in case of an error you emit an http.Error anyway and the
		// default status code returned is http.StatusOK, unless somethint else is set,
		// there is no need to deal with anything else manually.
		enc := json.NewEncoder(w)
		w.Header().Set("Content-Type", "application/json; charset=UTF-8")

		if err := enc.Encode(results); err != nil {
			LogError(w, err, "cannot marshal inbound result: %s", http.StatusInternalServerError)
		}
	})
}
