package newsfeedserver

import (
        "os"
        "fmt"
	"time"
	"reflect"
	"strconv"
	"net/http"
	"encoding/json"
	"github.com/google/uuid"
	"github.com/gorilla/mux"
	"github.com/gocql/gocql"
	"gopkg.in/olivere/elastic.v3"
)

type OutboundStoryDocument struct {
     	Id string `json:"id"`
	Sender string `json:"sender"`
	Story string `json:"story"`
}

func AddOutbound(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
   	decoder := json.NewDecoder(r.Body)
    	var ob Outbound
    	err := decoder.Decode(&ob)
	if err != nil {
	   fmt.Fprintf(w, "outbound body error: %s", err)
	   w.WriteHeader(http.StatusBadRequest)
	   return
	}
	cluster := gocql.NewCluster(os.Getenv("NOSQL_HOST"))
	cluster.Keyspace = os.Getenv("NOSQL_KEYSPACE")
	session, _ := cluster.CreateSession()
	defer session.Close()
	id := strconv.FormatInt(ob.From, 10)
	_, friends, err := GetFriendsInner(id)
	if err != nil {
	   fmt.Fprintf(w, "system error while fetching friends for %s", id)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	eshost := fmt.Sprintf("http://%s:9200", os.Getenv("SEARCH_HOST"))
	esclient, err := elastic.NewClient(elastic.SetURL(eshost))
	if err != nil {
	   fmt.Fprintf(w, "cannot connect to elasticsearch: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	esidr, err := uuid.NewRandom()
	if err != nil {
	   fmt.Fprintf(w, "cannot generate a random id: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	esid := fmt.Sprintf("%s", esidr)
	for _, friend := range friends {
	   inb := Inbound {
	      From: ob.From,
	      To: friend.To,
	      Occurred: ob.Occurred,
	      Subject: ob.Subject,
	      Story: ob.Story,
	   }
	   AddInbound(inb, session)
	}
	stmt := session.Query("insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?) using ttl 7776000", ob.From, ob.Subject, ob.Story)
	stmt.Consistency(gocql.One)
	stmt.Exec()
	osd := OutboundStoryDocument{
	    Id: esid,
	    Sender: id,
	    Story: ob.Story,
	}
	esclient.Index().
		Index("feed").
		Type("stories").
		Id(esid).
		BodyJson(osd).
		Do()
	w.WriteHeader(http.StatusOK)
}

func GetOutbound(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	cluster := gocql.NewCluster(os.Getenv("NOSQL_HOST"))
	cluster.Keyspace = os.Getenv("NOSQL_KEYSPACE")
	session, _ := cluster.CreateSession()
	defer session.Close()

	vars := mux.Vars(r)
	from, err := strconv.ParseInt(vars["id"], 0, 16)
	if err != nil {
	    fmt.Fprintf(w, "id is not an integer: %s", err)
	    w.WriteHeader(http.StatusBadRequest)
	    return
	}
	stmt := session.Query("select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc", vars["id"])
	stmt.Consistency(gocql.One)
	iter := stmt.Iter()
	defer iter.Close()
	var occurred time.Time
	var subject string
	var story string
	var results []Outbound
	for iter.Scan(&occurred, &subject, &story) {
	    ob := Outbound {
	      From: from,
	      Occurred: occurred,
	      Subject: subject,
	      Story: story,
	    }
	    results = append(results, ob)
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

func SearchOutbound(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	keywords, ok := r.URL.Query()["keywords"]
	if !ok || len(keywords[0]) < 1 {
	   fmt.Fprint(w, "must specify keywords")
	   w.WriteHeader(http.StatusBadRequest)
	   return
	}
	eshost := fmt.Sprintf("http://%s:9200", os.Getenv("SEARCH_HOST"))
	esclient, err := elastic.NewClient(elastic.SetURL(eshost))
	if err != nil {
	   fmt.Fprintf(w, "cannot connect to elasticsearch: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	query := elastic.NewMatchQuery("story", string(keywords[0]))
	searchResult, err := esclient.Search().
		      Index("feed").
		      Query(query).
		      Do()
	if err != nil {
	   fmt.Fprintf(w, "cannot query elasticsearch: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return	   
	}
	var osd OutboundStoryDocument
	var results []string
	for _, result := range searchResult.Each(reflect.TypeOf(osd)) {
	    doc, ok := result.(OutboundStoryDocument)
	    if ok {
	       results = append(results, doc.Sender)
	    }
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
