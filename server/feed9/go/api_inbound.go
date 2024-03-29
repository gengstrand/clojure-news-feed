package newsfeedserver

import (
        "fmt"
	"time"
	"strconv"
	"net/http"
	"encoding/json"
	"github.com/gorilla/mux"
	"github.com/gocql/gocql"
)

func GetInbound(w http.ResponseWriter, r *http.Request) {
        cw := connectCassandra()
        ew := LogWrapper{
	   Writer: w,
	}
	vars := mux.Vars(r)
	i, err := strconv.ParseInt(vars["id"], 0, 64)
	if err != nil {
	    ew.LogError(err, "id is not an integer: %s", http.StatusInternalServerError)
	    return
	}
	stmt := cw.Session.Query("select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc", vars["id"])
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
	      From: ToLink(from),
	      To: ToLink(i),
	      Occurred: FormatTimeToString(occurred),
	      Subject: subject,
	      Story: story,
	    }
	    results = append(results, inb)
	}
	resultb, err := json.Marshal(results)
	if err != nil {
	    ew.LogError(err, "cannot marshal inbound result: %s", http.StatusInternalServerError)
	    return
	}
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	fmt.Fprint(w, string(resultb))
	w.WriteHeader(http.StatusOK)
}
