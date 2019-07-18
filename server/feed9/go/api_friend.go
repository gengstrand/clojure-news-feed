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
	_ "github.com/go-sql-driver/mysql"
	"github.com/go-redis/redis"
)

func AddFriend(w http.ResponseWriter, r *http.Request) {
   	decoder := json.NewDecoder(r.Body)
    	var f Friend
    	err := decoder.Decode(&f)
	if err != nil {
	   msg := fmt.Sprintf("friend body error: %s", err)
	   log.Println(msg)
	   http.Error(w, msg, http.StatusBadRequest)
	   return
	}
	dbhost := fmt.Sprintf("feed:feed1234@tcp(%s:3306)/feed", os.Getenv("MYSQL_HOST"))
	db, err := sql.Open("mysql", dbhost)
	if err != nil {
	   msg := fmt.Sprintf("cannot open the database: %s", err)
	   log.Println(msg)
	   http.Error(w, msg, http.StatusInternalServerError)
	   return
	}
	defer db.Close()
	stmt, err := db.Prepare("call UpsertFriends(?, ?)")
	if err != nil {
	   msg := fmt.Sprintf("cannot prepare the friend upsert statement: %s", err)
	   log.Println(msg)
	   http.Error(w, msg, http.StatusInternalServerError)
	   return
	}
	defer stmt.Close()
	rows, err := stmt.Query(f.To, f.From)
	if err != nil {
	   msg := fmt.Sprintf("cannot insert friend: %s", err)
	   log.Println(msg)
	   http.Error(w, msg, http.StatusInternalServerError)
	   return
	}
	defer rows.Close()
	var id string
	for rows.Next() {
	    err := rows.Scan(&id)
	    if err != nil {
	       msg := fmt.Sprintf("cannot fetch friend data: %s", err)
	       log.Println(msg)
	       http.Error(w, msg, http.StatusInternalServerError)
	       return
	    }
	    i, err := strconv.ParseInt(id, 0, 64)
	    if err != nil {
	       msg := fmt.Sprintf("id is not an integer: %s", err)
	       log.Println(msg)
	       http.Error(w, msg, http.StatusInternalServerError)
	       return
	    }
	    f.Id = i
	    result, err := json.Marshal(f)
	    if err != nil {
	       msg := fmt.Sprintf("cannot marshal friend response: %s", err)
	       log.Println(msg)
	       http.Error(w, msg, http.StatusInternalServerError)
	       return
	    }
	    cacheHost := fmt.Sprintf("%s:6379", os.Getenv("CACHE_HOST"))
	    cache := redis.NewClient(&redis.Options{
	      	  Addr: cacheHost,
	      	  Password: "",
	      	  DB: 0,
	    })
	    cache.Del(fmt.Sprintf("Friends::%d", f.From)).Result()
	    cache.Del(fmt.Sprintf("Friends::%d", f.To)).Result()
	    w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	    fmt.Fprint(w, string(result))
	    w.WriteHeader(http.StatusOK)
	    return
	}
	log.Print("cannot retrieve pk from upsert friend")
	w.WriteHeader(http.StatusNoContent)
}

func GetFriendsFromDB(id string) ([]Friend, error) {
	dbhost := fmt.Sprintf("feed:feed1234@tcp(%s:3306)/feed", os.Getenv("MYSQL_HOST"))
	db, err := sql.Open("mysql", dbhost)
	if err != nil {
	   log.Printf("cannot open the database: %s", err)
	   return nil, err
	}
	defer db.Close()
	stmt, err := db.Prepare("call FetchFriends(?)")
	if err != nil {
	   log.Printf("cannot prepare the fetch friends statement: %s", err)
	   return nil, err
	}
	defer stmt.Close()
	i, err := strconv.ParseInt(id, 0, 64)
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
	       log.Printf("cannot fetch friend data: %s", err)
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
	cacheHost := fmt.Sprintf("%s:6379", os.Getenv("CACHE_HOST"))
	cache := redis.NewClient(&redis.Options{
	      Addr: cacheHost,
	      Password: "",
	      DB: 0,
	})
	defer cache.Close()
	key := "Friends::" + id
	val, err := cache.Get(key).Result()
	if err == redis.Nil {
	   results, err := GetFriendsFromDB(id)
	   if err != nil {
	      return "", nil, err
	   } else {
	      resultb, err := json.Marshal(results)
	      if err != nil {
	      	 log.Printf("cannot marshal friends response: %s", err)
	    	 return "", nil, err
	      }
	      response := string(resultb)
	      cache.Set("Friends::" + id, response, 0)
	      return response, results, nil
	   }
	} else if err != nil {
	   log.Printf("cannot fetch %s from cache: %s", key, err)
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
	vars := mux.Vars(r)
	result, _, err := GetFriendsInner(vars["id"])
	if err != nil {
	   msg := fmt.Sprintf("system error while getting friend %s", vars["id"])
	   log.Println(msg)
	   http.Error(w, msg, http.StatusInternalServerError)
	} else {
	   w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	   fmt.Fprint(w, result)
	   w.WriteHeader(http.StatusOK)
	}
}
