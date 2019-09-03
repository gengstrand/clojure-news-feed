package newsfeedserver

import (
	"os"
        "fmt"
	"log"
	"strconv"
	"net/http"
	"database/sql"
	"encoding/json"
	"github.com/gorilla/mux"
	"github.com/go-redis/redis"
)

func AddParticipant(w http.ResponseWriter, r *http.Request) {
   	decoder := json.NewDecoder(r.Body)
    	var p Participant
    	err := decoder.Decode(&p)
	if err != nil {
	   LogError(w, err, "participant body error: %s", http.StatusBadRequest)
	   return
	}
	db, err := MySqlConnect()
	if err != nil {
	   LogError(w, err, "cannot open the database: %s", http.StatusInternalServerError)
	   return
	}
	stmt, err := db.Prepare("call UpsertParticipant(?)")
	if err != nil {
	   LogError(w, err, "cannot prepare the upsert statement: %s", http.StatusInternalServerError)
	   return
	}
	defer stmt.Close()
	rows, err := stmt.Query(p.Name)
	if err != nil {
	   LogError(w, err, "cannot insert participant: %s", http.StatusInternalServerError)
	   return
	}
	defer rows.Close()
	var id string
	defer MySqlDisconnect(db)
	for rows.Next() {
	    err := rows.Scan(&id)
	    if err != nil {
	       LogError(w, err, "cannot fetch participant pk: %s", http.StatusInternalServerError)
	       return
	    }
	    i, err := strconv.ParseInt(id, 0, 64)
	    if err != nil {
	       LogError(w, err, "id is not an integer: %s", http.StatusInternalServerError)
	       return
	    }
	    p.Id = i
	    result, err := json.Marshal(p)
	    if err != nil {
	       LogError(w, err, "cannot marshal participant response: %s", http.StatusInternalServerError)
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

func GetParticipantFromDB(id string, cache *redis.Client, w http.ResponseWriter) {
	dbhost := fmt.Sprintf("feed:feed1234@tcp(%s:3306)/feed", os.Getenv("MYSQL_HOST"))
	db, err := sql.Open("mysql", dbhost)
	if err != nil {
	   LogError(w, err, "cannot open the database: %s", http.StatusInternalServerError)
	   return
	}
	defer db.Close()
	stmt, err := db.Prepare("call FetchParticipant(?)")
	if err != nil {
	   LogError(w, err, "cannot prepare the participant fetch statement: %s", http.StatusInternalServerError)
	   return
	}
	defer stmt.Close()
	i, err := strconv.ParseInt(id, 0, 64)
	if err != nil {
	    LogError(w, err, "id is not an integer: %s", http.StatusBadRequest)
	    return
	}
	rows, err := stmt.Query(id)
	if err != nil {
	   LogError(w, err, "cannot query for participant: %s", http.StatusInternalServerError)
	   return
	}
	defer rows.Close()
	var name string
	for rows.Next() {
	    err := rows.Scan(&name)
  	    if err != nil {
	       LogError(w, err, "cannot fetch participant data: %s", http.StatusInternalServerError)
	       return
	    }
	    p := Participant{
	      Id: i,
	      Name: name,
	    }
	    resultb, err := json.Marshal(p)
	    if err != nil {
	       LogError(w, err, "cannot marshal participant response: %s", http.StatusInternalServerError)
	       return
	    }
	    result := string(resultb)
	    w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	    fmt.Fprint(w, result)
	    cache.Set("Participant::" + id, result, 0)
	    w.WriteHeader(http.StatusOK)
	    return
	}
	w.WriteHeader(http.StatusNotFound)
}

func GetParticipant(w http.ResponseWriter, r *http.Request) {
	cacheHost := fmt.Sprintf("%s:6379", os.Getenv("CACHE_HOST"))
	cache := redis.NewClient(&redis.Options{
	      Addr: cacheHost,
	      Password: "",
	      DB: 0,
	})
	defer cache.Close()
	vars := mux.Vars(r)
	key := "Participant::" + vars["id"]
	val, err := cache.Get(key).Result()
	if err == redis.Nil {
	   GetParticipantFromDB(vars["id"], cache, w)
	} else if err != nil {
	   LogError(w, err, "cannot fetch participant from cache: %s", http.StatusInternalServerError)
	   return
	} else {
	   w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	   fmt.Fprintf(w, val)
	   w.WriteHeader(http.StatusOK)
	}
}
