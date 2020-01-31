package newsfeedserver

import (
        "fmt"
	"log"
	"strconv"
	"net/http"
	"encoding/json"
	"github.com/gorilla/mux"
	_ "github.com/go-sql-driver/mysql"
	"github.com/go-redis/redis"
)

func AddParticipant(w http.ResponseWriter, r *http.Request) {
        ew := LogWrapper{
	   Writer: w,
	}
   	decoder := json.NewDecoder(r.Body)
    	var p Participant
    	err := decoder.Decode(&p)
	if err != nil {
	  ew. LogError(err, "participant body error: %s", http.StatusBadRequest)
	   return
	}
	stmt, err := db.Prepare("call UpsertParticipant(?)")
	if err != nil {
	   ew.LogError(err, "cannot prepare the upsert statement: %s", http.StatusInternalServerError)
	   return
	}
	defer stmt.Close()
	rows, err := stmt.Query(p.Name)
	if err != nil {
	   ew.LogError(err, "cannot insert participant: %s", http.StatusInternalServerError)
	   return
	}
	defer rows.Close()
	var id string
	for rows.Next() {
	    err := rows.Scan(&id)
	    if err != nil {
	       ew.LogError(err, "cannot fetch participant pk: %s", http.StatusInternalServerError)
	       return
	    }
	    i, err := strconv.ParseInt(id, 0, 64)
	    if err != nil {
	       ew.LogError(err, "id is not an integer: %s", http.StatusInternalServerError)
	       return
	    }
	    p.Id = i
	    p.Link = ToLink(i)
	    result, err := json.Marshal(p)
	    if err != nil {
	       ew.LogError(err, "cannot marshal participant response: %s", http.StatusInternalServerError)
	       return
	    }
	    w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	    fmt.Fprint(w, string(result))
	    w.WriteHeader(http.StatusOK)
	    return
	}
	log.Print("cannot retrieve pk from upsert participant")
	w.WriteHeader(http.StatusNoContent)
}

func GetParticipantFromDB(id string, rw RedisWrapper, w http.ResponseWriter) {
        ew := LogWrapper{
	   Writer: w,
	}
	stmt, err := db.Prepare("call FetchParticipant(?)")
	if err != nil {
	   ew.LogError(err, "cannot prepare the participant fetch statement: %s", http.StatusInternalServerError)
	   return
	}
	defer stmt.Close()
	i, err := strconv.ParseInt(id, 0, 64)
	if err != nil {
	    ew.LogError(err, "id is not an integer: %s", http.StatusBadRequest)
	    return
	}
	rows, err := stmt.Query(id)
	if err != nil {
	   ew.LogError(err, "cannot query for participant: %s", http.StatusInternalServerError)
	   return
	}
	defer rows.Close()
	var name string
	for rows.Next() {
	    err := rows.Scan(&name)
  	    if err != nil {
	       ew.LogError(err, "cannot fetch participant data: %s", http.StatusInternalServerError)
	       return
	    }
	    p := Participant{
	      Id: i,
	      Name: name,
	    }
	    resultb, err := json.Marshal(p)
	    if err != nil {
	       ew.LogError(err, "cannot marshal participant response: %s", http.StatusInternalServerError)
	       return
	    }
	    result := string(resultb)
	    w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	    fmt.Fprint(w, result)
	    rw.Set("Participant::" + id, result, 0)
	    w.WriteHeader(http.StatusOK)
	    return
	}
	w.WriteHeader(http.StatusNotFound)
}

func GetParticipant(w http.ResponseWriter, r *http.Request) {
        ew := LogWrapper{
	   Writer: w,
	}
        rw := connectRedis()
	vars := mux.Vars(r)
	key := "Participant::" + vars["id"]
	val, err := rw.Get(key)
	if err == redis.Nil {
	   GetParticipantFromDB(vars["id"], rw, w)
	} else if err != nil {
	   ew.LogError(err, "cannot fetch participant from cache: %s", http.StatusInternalServerError)
	   return
	} else {
	   w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	   fmt.Fprintf(w, val)
	   w.WriteHeader(http.StatusOK)
	}
}
