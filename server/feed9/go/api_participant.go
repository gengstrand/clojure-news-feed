package newsfeedserver

import (
        "fmt"
	"strconv"
	"net/http"
	"database/sql"
	"encoding/json"
	"github.com/gorilla/mux"
	_ "github.com/go-sql-driver/mysql"
	"github.com/go-redis/redis"
)

func AddParticipant(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
   	decoder := json.NewDecoder(r.Body)
    	var p Participant
    	err := decoder.Decode(&p)
	if err != nil {
	   fmt.Fprintf(w, "participant body error: %s", err)
	   w.WriteHeader(http.StatusBadRequest)
	   return
	}
	db, err := sql.Open("mysql", "feed:feed1234@tcp(mysql:3306)/feed")
	if err != nil {
	   fmt.Fprintf(w, "cannot open the database: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	defer db.Close()
	stmt, err := db.Prepare("call UpsertParticipant(?)")
	if err != nil {
	   fmt.Fprintf(w, "cannot prepare the upsert statement: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	defer stmt.Close()
	rows, err := stmt.Query(p.Name)
	if err != nil {
	   fmt.Fprintf(w, "cannot insert participant: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	defer rows.Close()
	var id string
	for rows.Next() {
	    err := rows.Scan(&id)
	    if err != nil {
	       fmt.Fprintf(w, "cannot fetch data: %s", err)
	       w.WriteHeader(http.StatusInternalServerError)
	       return
	    }
	    i, err := strconv.ParseInt(id, 0, 16)
	    if err != nil {
	       fmt.Fprintf(w, "id is not an integer: %s", err)
	       w.WriteHeader(http.StatusInternalServerError)
	       return
	    }
	    p.Id = i
	    result, err := json.Marshal(p)
	    if err != nil {
	       fmt.Fprintf(w, "cannot marshal data: %s", err)
	       w.WriteHeader(http.StatusInternalServerError)
	       return
	    }
	    fmt.Fprint(w, string(result))
	    w.WriteHeader(http.StatusOK)
	    return
	}
	w.WriteHeader(http.StatusNoContent)
}

func GetParticipantFromDB(id string, cache *redis.Client, w http.ResponseWriter) {
	db, err := sql.Open("mysql", "feed:feed1234@tcp(mysql:3306)/feed")
	if err != nil {
	   fmt.Fprintf(w, "cannot open the database: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	defer db.Close()
	stmt, err := db.Prepare("call FetchParticipant(?)")
	if err != nil {
	   fmt.Fprintf(w, "cannot prepare the fetch statement: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	defer stmt.Close()
	i, err := strconv.ParseInt(id, 0, 16)
	if err != nil {
	    fmt.Fprintf(w, "id is not an integer: %s", err)
	    w.WriteHeader(http.StatusBadRequest)
	    return
	}
	rows, err := stmt.Query(id)
	if err != nil {
	   fmt.Fprintf(w, "cannot query for participant: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	defer rows.Close()
	var name string
	for rows.Next() {
	    err := rows.Scan(&name)
  	    if err != nil {
	       fmt.Fprintf(w, "cannot fetch data: %s", err)
	       w.WriteHeader(http.StatusInternalServerError)
	       return
	    }
	    p := Participant{
	      Id: i,
	      Name: name,
	    }
	    resultb, err := json.Marshal(p)
	    if err != nil {
	       fmt.Fprintf(w, "cannot marshal data: %s", err)
	       w.WriteHeader(http.StatusInternalServerError)
	       return
	    }
	    result := string(resultb)
	    fmt.Fprint(w, result)
	    cache.Set("Participant::" + id, result, 0)
	    w.WriteHeader(http.StatusOK)
	    return
	}
	w.WriteHeader(http.StatusNotFound)
}

func GetParticipant(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	cache := redis.NewClient(&redis.Options{
	      Addr: "redis:6379",
	      Password: "",
	      DB: 0,
	})
	vars := mux.Vars(r)
	val, err := cache.Get("Participant::" + vars["id"]).Result()
	if err == redis.Nil {
	   GetParticipantFromDB(vars["id"], cache, w)
	} else if err != nil {
	   fmt.Fprintf(w, "cannot fetch from cache: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	} else {
	   fmt.Fprintf(w, val)
	   w.WriteHeader(http.StatusOK)
	}
}
