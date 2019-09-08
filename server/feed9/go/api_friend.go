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

func AddFriend(w http.ResponseWriter, r *http.Request) {
        ew := LogWrapper{
	   Writer: w,
	}
   	decoder := json.NewDecoder(r.Body)
    	var f Friend
    	err := decoder.Decode(&f)
	if err != nil {
	   ew.LogError(err, "friend body error: %s", http.StatusBadRequest)
	   return
	}
	stmt, err := db.Prepare("call UpsertFriends(?, ?)")
	if err != nil {
	   ew.LogError(err, "cannot prepare the friend upsert statement: %s", http.StatusInternalServerError)
	   return
	}
	defer stmt.Close()
	rows, err := stmt.Query(f.To, f.From)
	if err != nil {
	   ew.LogError(err, "cannot insert friend: %s", http.StatusInternalServerError)
	   return
	}
	defer rows.Close()
	var id string
	for rows.Next() {
	    err := rows.Scan(&id)
	    if err != nil {
	       ew.LogError(err, "cannot fetch friend data: %s", http.StatusInternalServerError)
	       return
	    }
	    i, err := strconv.ParseInt(id, 0, 64)
	    if err != nil {
	       ew.LogError(err, "id is not an integer: %s", http.StatusInternalServerError)
	       return
	    }
	    f.Id = i
	    result, err := json.Marshal(f)
	    if err != nil {
	       ew.LogError(err, "cannot marshal friend response: %s", http.StatusInternalServerError)
	       return
	    }
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

func GetFriendsInner(id string, cw CacheWrapper, gsw GetSqlWrapper) (string, []Friend, error) {
	key := "Friends::" + id
	val, err := cw.Get(key)
	if err == redis.Nil {
	   results, err := gsw.FetchFriends(id)
	   if err != nil {
	      return "", nil, err
	   } else {
	      resultb, err := json.Marshal(results)
	      if err != nil {
	      	 log.Printf("cannot marshal friends response: %s", err)
	    	 return "", nil, err
	      }
	      response := string(resultb)
	      cw.Set(key, response, 0)
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
	rw := connectRedis()
	dbw := connectMysql()
	result, _, err := GetFriendsInner(vars["id"], rw, dbw)
	if err != nil {
	   msg := fmt.Sprintf("system error while getting friend %s: %s", vars["id"], err)
	   log.Println(msg)
	   http.Error(w, msg, http.StatusInternalServerError)
	} else {
	   w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	   fmt.Fprint(w, result)
	   w.WriteHeader(http.StatusOK)
	}
}
