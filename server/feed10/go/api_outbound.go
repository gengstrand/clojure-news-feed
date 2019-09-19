package newsfeedserver

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"reflect"
	"strconv"
	"sync"
	"time"

	"github.com/go-redis/redis"
	"github.com/gocql/gocql"
	"github.com/google/uuid"
	"github.com/gorilla/mux"
	"gopkg.in/olivere/elastic.v3"
)

type OutboundStoryDocument struct {
	Id     string `json:"id"`
	Sender string `json:"sender"`
	Story  string `json:"story"`
}

var indexerCount int = 3

// ElasticSearchIndexer must be buffered in order to be able
// to utlize the configured instances.
var ElasticSearchIndexer = make(chan OutboundStoryDocument, indexerCount)

func handleIndexRequest(esclient *elastic.Client) {

	for req := range ElasticSearchIndexer {
		esclient.Index().
			Index("feed").
			Type("stories").
			Id(req.Id).
			BodyJson(req).
			Do()
	}
}

func SetupIndexers(esclient *elastic.Client) {
	for i := 0; i < indexerCount; i++ {
		go handleIndexRequest(esclient)
	}
}

func AddOutbound(session *gocql.Session, db *sql.DB, cache *redis.Client) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		decoder := json.NewDecoder(r.Body)
		var ob Outbound
		err := decoder.Decode(&ob)
		if err != nil {
			LogError(w, err, "outbound body error: %s", http.StatusBadRequest)
			return
		}

		id := strconv.FormatInt(ob.From, 10)
		_, friends, err := GetFriendsInner(db, cache, id)
		if err != nil {
			LogError(w, err, "system error while fetching friends for %s", http.StatusInternalServerError)
			return
		}
		esidr, err := uuid.NewRandom()
		if err != nil {
			LogError(w, err, "cannot generate a random id: %s", http.StatusInternalServerError)
			return
		}
		esid := fmt.Sprintf("%s", esidr)
		for _, friend := range friends {
			inb := Inbound{
				From:     ob.From,
				To:       friend.To,
				Occurred: ob.Occurred,
				Subject:  ob.Subject,
				Story:    ob.Story,
			}
			log.Println("%#v", inb)
			AddInbound(inb, session)
		}
		stmt := session.Query("insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?) using ttl 7776000", ob.From, ob.Subject, ob.Story)
		stmt.Consistency(gocql.One)
		stmt.Exec()
		osd := OutboundStoryDocument{
			Id:     esid,
			Sender: id,
			Story:  ob.Story,
		}
		ElasticSearchIndexer <- osd
		w.Header().Set("Content-Type", "application/json; charset=UTF-8")
		enc := json.NewEncoder(w)
		if err := enc.Encode(ob); err != nil {
			LogError(w, err, "cannot marshal outbound response: %s", http.StatusInternalServerError)
			return
		}
	})
}

func GetOutbound(session *gocql.Session) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {

		vars := mux.Vars(r)
		from, err := strconv.ParseInt(vars["id"], 0, 64)
		if err != nil {
			LogError(w, err, "id is not an integer: %s", http.StatusBadRequest)
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
			ob := Outbound{
				From:     from,
				Occurred: occurred,
				Subject:  subject,
				Story:    story,
			}
			results = append(results, ob)
		}

		// We use an encoder since we want to write the result to the ResultWriter
		// anyway. Since in case of an error you emit an http.Error anyway and the
		// default status code returned is http.StatusOK, unless somethint else is set,
		// there is no need to deal with anything else manually.
		enc := json.NewEncoder(w)

		w.Header().Set("Content-Type", "application/json; charset=UTF-8")
		if err := enc.Encode(results); err != nil {
			LogError(w, err, "cannot marshal outbound response: %s", http.StatusInternalServerError)
		}

	})
}

func SearchOutbound(esPool *sync.Pool) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		keywords, ok := r.URL.Query()["keywords"]
		if !ok || len(keywords[0]) < 1 {
			msg := fmt.Sprint("must specify keywords")
			log.Println(msg)
			http.Error(w, msg, http.StatusBadRequest)
			return
		}
		esclient := esPool.Get().(*elastic.Client)
		query := elastic.NewMatchQuery("story", string(keywords[0]))
		searchResult, err := esclient.Search().
			Index("feed").
			Query(query).
			Do()
		// Moved up, because in an case of an error, the client is not returned.
		// Alternatively, this could be deferred.
		esPool.Put(esclient)
		if err != nil {
			LogError(w, err, "cannot query elasticsearch: %s", http.StatusInternalServerError)
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

		w.Header().Set("Content-Type", "application/json; charset=UTF-8")
		enc := json.NewEncoder(w)
		if len(results) == 0 {
			w.Write([]byte("{}"))
			return
		}
		if err := enc.Encode(results); err != nil {
			LogError(w, err, "cannot marshal outbound results: %s", http.StatusInternalServerError)
			return
		}

	})
}
