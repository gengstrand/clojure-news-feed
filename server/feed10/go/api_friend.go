package newsfeedserver

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"

	"github.com/go-redis/redis"

	// MySQL driver for database/sql
	// Comment required by golint.
	_ "github.com/go-sql-driver/mysql"
)

func AddFriend(db *sql.DB, cache *redis.Client) http.Handler {

	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		decoder := json.NewDecoder(r.Body)
		var f Friend
		err := decoder.Decode(&f)
		if err != nil {
			LogError(w, err, "friend body error: %s", http.StatusBadRequest)
			return
		}

		rows, err := UpsertFriendsStmt.Query(f.To, f.From)
		if err != nil {
			LogError(w, err, "cannot insert friend: %s", http.StatusInternalServerError)
			return
		}

		defer rows.Close()
		var id string
		for rows.Next() {
			err := rows.Scan(&id)
			if err != nil {
				LogError(w, err, "cannot fetch friend data: %s", http.StatusInternalServerError)
				return
			}
			i, err := strconv.ParseInt(id, 0, 64)
			if err != nil {
				LogError(w, err, "id is not an integer: %s", http.StatusInternalServerError)
				return
			}
			f.Id = i
			result, err := json.Marshal(f)
			if err != nil {
				LogError(w, err, "cannot marshal friend response: %s", http.StatusInternalServerError)
				return
			}
			cache.Del(fmt.Sprintf("Friends::%d", f.From)).Result()
			cache.Del(fmt.Sprintf("Friends::%d", f.To)).Result()
			w.Header().Set("Content-Type", "application/json; charset=UTF-8")
			w.Write(result)
			return
		}
		log.Print("cannot retrieve pk from upsert friend")

		w.WriteHeader(http.StatusNoContent)
	})
}

func GetFriendsFromDB(db *sql.DB, id string) ([]Friend, error) {

	i, err := strconv.ParseInt(id, 0, 64)
	if err != nil {
		log.Printf("id is not an integer: %s", err)
		return nil, err
	}
	rows, err := FetchFriendsStmt.Query(id)
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
			Id:   fid,
			From: i,
			To:   pid,
		}
		results = append(results, f)
	}
	return results, nil
}

func GetFriendsInner(db *sql.DB, cache *redis.Client, id string) ([]byte, []Friend, error) {

	key := "Friends::" + id
	val, err := cache.Get(key).Bytes()

	if err == redis.Nil {
		results, err := GetFriendsFromDB(db, id)
		if err != nil {
			return nil, nil, err
		}

		resultb, err := json.Marshal(results)
		if err != nil {
			log.Printf("cannot marshal friends response: %s", err)
			return nil, nil, err
		}
		cache.Set("Friends::"+id, resultb, 0)
		return resultb, results, nil

	} else if err != nil {
		log.Printf("cannot fetch %s from cache: %s", key, err)
		return nil, nil, err
	}

	var friends []Friend

	// It is HIGHLY questionable wether the unmarshalling
	// does any good, since you should have received a
	// an error while encoding the data, especially since
	// friends is ignored during return even if the Unmarshal
	// is successful.
	if err := json.Unmarshal([]byte(val), &friends); err != nil {
		log.Printf("cannot parse friends from cache: %s", err)
		return val, nil, err
	}
	return val, friends, nil

}

func GetFriend(db *sql.DB, cache *redis.Client) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		result, _, err := GetFriendsInner(db, cache, vars["id"])
		if err != nil {
			msg := fmt.Sprintf("system error while getting friend %s: %s", vars["id"], err)
			log.Println(msg)
			http.Error(w, msg, http.StatusInternalServerError)
			return
		}
		w.Header().Set("Content-Type", "application/json; charset=UTF-8")
		w.Write(result)
	})
}
