package newsfeedserver

import (
        "os"
        "fmt"
	"log"
	"time"
	"sync"
	"errors"
	"strings"
	"reflect"
	"strconv"
	"net/http"
	"encoding/json"
	"github.com/google/uuid"
	"github.com/gorilla/mux"
	"github.com/gocql/gocql"
	"gopkg.in/olivere/elastic.v3"
)

var esPool = &sync.Pool{
	New: func() interface{} {
		eshost := fmt.Sprintf("http://%s:9200", os.Getenv("SEARCH_HOST"))
		esclient, err := elastic.NewClient(elastic.SetURL(eshost))
		if err != nil {
	   	   log.Printf("cannot connect to elasticsearch: %s", err)
		}
		return esclient
	},
}

type OutboundStoryDocument struct {
     	Id string `json:"id"`
	Sender int64 `json:"sender"`
	Story string `json:"story"`
}

var ElasticSearchIndexer = make(chan OutboundStoryDocument)

func handleIndexRequest() {
	eshost := fmt.Sprintf("http://%s:9200", os.Getenv("SEARCH_HOST"))
	esclient, err := elastic.NewClient(elastic.SetURL(eshost))
	if err != nil {
	   log.Printf("cannot connect to elasticsearch: %s", err)
	}
	for {
     	    req := <- ElasticSearchIndexer
	    esclient.Index().
		Index("feed").
		Type("stories").
		Id(req.Id).
		BodyJson(req).
		Do()
        }
}

func init() {
        go handleIndexRequest()
        go handleIndexRequest()
        go handleIndexRequest()
}

func AddOutboundInner(ob Outbound, ew ErrorWrapper, aw AddCassandraWrapper, cw CacheWrapper, gsw GetSqlWrapper) {
	id := ObtainId(ob.From)
	if strings.Compare("", id) == 0 {
	   ew.LogError(errors.New("from is not a link"), "cannot extract id out of from: %s", http.StatusBadRequest)
	   return
	}
	_, friends, err := GetFriendsInner(id, cw, gsw)
	if err != nil {
	   ew.LogError(err, "system error while fetching friends for %s", http.StatusInternalServerError)
	   return
	}
	for _, friend := range friends {
	   inb := Inbound {
	      From: ob.From,
	      To: friend.To,
	      Occurred: ob.Occurred,
	      Subject: ob.Subject,
	      Story: ob.Story,
	   }
	   aw.AddInbound(inb)
	}
	aw.AddOutbound(ob)
}

func AddOutbound(w http.ResponseWriter, r *http.Request) {
        ew := LogWrapper{
	   Writer: w,
	}
	vars := mux.Vars(r)
   	decoder := json.NewDecoder(r.Body)
    	var ob Outbound
    	err := decoder.Decode(&ob)
	if err != nil {
	    ew.LogError(err, "outbound body error: %s", http.StatusBadRequest)
	    return
	}
	ob.From = Linkify(vars["id"])
	cw := connectCassandra()
	rw := connectRedis()
	dbw := connectMysql()
	esidr, err := uuid.NewRandom()
	if err != nil {
	   ew.LogError(err, "cannot generate a random id: %s", http.StatusInternalServerError)
	   return
	}
	esid := fmt.Sprintf("%s", esidr)
	from, err := strconv.ParseInt(vars["id"], 0, 64)
	if err != nil {
	    ew.LogError(err, "id is not an integer: %s", http.StatusBadRequest)
	    return
	}
	osd := OutboundStoryDocument{
	    Id: esid,
	    Sender: from,
	    Story: ob.Story,
	}
	AddOutboundInner(ob, ew, cw, rw, dbw)
	ElasticSearchIndexer <- osd
	resultb, err := json.Marshal(ob)
	if err != nil {
	    ew.LogError(err, "cannot marshal outbound response: %s", http.StatusInternalServerError)
	    return
	}
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	fmt.Fprint(w, string(resultb))
	w.WriteHeader(http.StatusOK)
}

func GetOutbound(w http.ResponseWriter, r *http.Request) {
        ew := LogWrapper{
	   Writer: w,
	}
	cw := connectCassandra()
	vars := mux.Vars(r)
	from, err := strconv.ParseInt(vars["id"], 0, 64)
	if err != nil {
	    ew.LogError(err, "id is not an integer: %s", http.StatusBadRequest)
	    return
	}
	stmt := cw.Session.Query("select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc", vars["id"])
	stmt.Consistency(gocql.One)
	iter := stmt.Iter()
	defer iter.Close()
	var occurred time.Time
	var subject string
	var story string
	var results []Outbound
	for iter.Scan(&occurred, &subject, &story) {
	    ob := Outbound {
	      From: ToLink(from),
	      Occurred: FormatTimeToString(occurred),
	      Subject: subject,
	      Story: story,
	    }
	    results = append(results, ob)
	}
	resultb, err := json.Marshal(results)
	if err != nil {
	   ew.LogError(err, "cannot marshal outbound response: %s", http.StatusInternalServerError)
	    return
	}
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	fmt.Fprint(w, string(resultb))
	w.WriteHeader(http.StatusOK)
}

func SearchOutbound(w http.ResponseWriter, r *http.Request) {
        ew := LogWrapper{
	   Writer: w,
	}
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
		      Size(1000).
		      Query(query).
		      Do()
	if err != nil {
	   ew.LogError(err, "cannot query elasticsearch: %s", http.StatusInternalServerError)
	   return
	}
	esPool.Put(esclient)
	var osd OutboundStoryDocument
	var results []string
	for _, result := range searchResult.Each(reflect.TypeOf(osd)) {
	    doc, ok := result.(OutboundStoryDocument)
	    if ok {
	       	results = append(results, ToLink(doc.Sender))
	    }
	}
	resultb, err := json.Marshal(results)
	if err != nil {
	    ew.LogError(err, "cannot marshal outbound results: %s", http.StatusInternalServerError)
	    return
	}
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	fmt.Fprint(w, string(resultb))
	w.WriteHeader(http.StatusOK)
}
