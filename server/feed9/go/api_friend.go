package newsfeedserver

import (
        "fmt"
	"log"
	"strconv"
	"net/http"
	"database/sql"
	"encoding/json"
	"github.com/gorilla/mux"
	_ "github.com/go-sql-driver/mysql"
	"github.com/go-redis/redis"
)

func AddFriend(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
   	decoder := json.NewDecoder(r.Body)
    	var f Friend
    	err := decoder.Decode(&f)
	if err != nil {
	   fmt.Fprintf(w, "friend body error: %s", err)
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
	stmt, err := db.Prepare("call UpsertFriends(?, ?)")
	if err != nil {
	   fmt.Fprintf(w, "cannot prepare the upsert statement: %s", err)
	   w.WriteHeader(http.StatusInternalServerError)
	   return
	}
	defer stmt.Close()
	rows, err := stmt.Query(f.To, f.From)
	if err != nil {
	   fmt.Fprintf(w, "cannot insert friend: %s", err)
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
	    f.Id = i
	    result, err := json.Marshal(f)
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

func GetFriendsFromDB(id string) ([]Friend, error) {
	db, err := sql.Open("mysql", "feed:feed1234@tcp(mysql:3306)/feed")
	if err != nil {
	   log.Printf("cannot open the database: %s", err)
	   return nil, err
	}
	defer db.Close()
	stmt, err := db.Prepare("call FetchFriends(?)")
	if err != nil {
	   log.Printf("cannot prepare the fetch statement: %s", err)
	   return nil, err
	}
	defer stmt.Close()
	i, err := strconv.ParseInt(id, 0, 16)
	if err != nil {
	    log.Printf("id is not an integer: %s", err)
	    return nil, err
	}
	rows, err := stmt.Query(id)
	if err != nil {
	   log.Printf("cannot query for friends: %s", err)
	   return nil, err
	}
	defer rows.Close()
	var fid int64
	var pid int64
	var results []Friend
	for rows.Next() {
	    err := rows.Scan(&fid, &pid)
  	    if err != nil {
	       log.Printf("cannot fetch data: %s", err)
	       return nil, err
	    }
	    f := Friend{
	      Id: fid,
	      From: i,
	      To: pid,
	    }
	    results = append(results, f)
	}
	return results, nil
}

func GetFriendsInner(id string) (string, []Friend, error) {
	cache := redis.NewClient(&redis.Options{
	      Addr: "redis:6379",
	      Password: "",
	      DB: 0,
	})
	val, err := cache.Get("Friends::" + id).Result()
	if err == redis.Nil {
	   results, err := GetFriendsFromDB(id)
	   if err != nil {
	      return "", nil, err
	   } else {
	      resultb, err := json.Marshal(results)
	      if err != nil {
	      	 log.Printf("cannot marshal data: %s", err)
	    	 return "", nil, err
	      }
	      response := string(resultb)
	      cache.Set("Friends::" + id, response, 0)
	      return response, results, nil
	   }
	} else if err != nil {
	   log.Printf("cannot fetch from cache: %s", err)
	   return "", nil, err
	} else {
	   var friends []Friend
    	   err := json.Unmarshal([]byte(val), &friends)
	   if err != nil {
	      log.Printf("cannot parse friends from cache: %s", err)
	      return val, nil, err
	   }
	   return val, friends, nil
	}
}

func GetFriend(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	vars := mux.Vars(r)
	result, _, err := GetFriendsInner(vars["id"])
	if err != nil {
	   fmt.Fprintf(w, "system error while getting friend %s", vars["id"])
	   w.WriteHeader(http.StatusInternalServerError)
	} else {
	   fmt.Fprint(w, result)
	   w.WriteHeader(http.StatusOK)
	}
}
