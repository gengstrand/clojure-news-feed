package newsfeedserver

import (
	"bytes"
	"database/sql"
	"encoding/json"
	"io"
	"log"
	"net/http"
	"strconv"

	"github.com/go-redis/redis"
	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux"
)

func AddParticipant(db *sql.DB) http.Handler {

	// The refactoring here is important.
	// The http.Handler returned still has
	// a reference to db, so it can use it.
	//
	// By returnign a handler instead of directly
	// implementing a func(http.ResponseWriter, *http.Request)
	// we have the best of both worlds.
	//
	// Note that we could also add the participant to the cache here:
	// We have all the data after looking it up.
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		decoder := json.NewDecoder(r.Body)
		var p Participant

		if err := decoder.Decode(&p); err != nil {
			LogError(w, err, "participant body error: %s", http.StatusBadRequest)
			return
		}

		rows, err := UpsertStmt.Query(p.Name)
		if err != nil {
			LogError(w, err, "cannot insert participant: %s", http.StatusInternalServerError)
			return
		}
		defer rows.Close()

		var id string

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

			// We use an encoder directly on the response writer
			// The reason for this is that you are not required to
			// set the http.StatusOK header manually.
			// Unless you explicitly return an error, http.StatusOK
			// is the default reponse code.
			// With this, we save us some allocations as we do not
			// need to write in a buffer.
			enc := json.NewEncoder(w)
			w.Header().Set("Content-Type", "application/json; charset=UTF-8")
			if enc.Encode(p) != nil {
				LogError(w, err, "cannot marshal participant response: %s", http.StatusInternalServerError)
			}
			return
		}
		log.Print("cannot retrieve pk from upsert participant")
		w.WriteHeader(http.StatusNoContent)
	})
}

func GetParticipantFromDB(id string, db *sql.DB, cache *redis.Client, w http.ResponseWriter) {
	var err error

	i, err := strconv.ParseInt(id, 0, 64)
	if err != nil {
		LogError(w, err, "id is not an integer: %s", http.StatusBadRequest)
		return
	}

	rows, err := FetchParticipantStmt.Query(id)

	if err != nil {
		LogError(w, err, "cannot query for participant: %s", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var name string
	for rows.Next() {

		if err = rows.Scan(&name); err != nil {
			LogError(w, err, "cannot fetch participant data: %s", http.StatusInternalServerError)
			return
		}

		p := Participant{
			Id:   i,
			Name: name,
		}

		buf := bytes.NewBuffer(nil)
		w.Header().Set("Content-Type", "application/json; charset=UTF-8")

		// Using a Multiwriter, we actually fill
		// buf and respond simultaneously.
		mw := io.MultiWriter(buf, w)

		enc := json.NewEncoder(mw)
		if err = enc.Encode(p); err != nil {
			LogError(w, err, "cannot marshal participant response: %s", http.StatusInternalServerError)
			return
		}
		// Note that we use bytes here instead of strings, as this saves us
		// some back-and-forth conversion.
		cache.Set("Participant::"+id, buf.Bytes(), 0)
		return
	}
	w.WriteHeader(http.StatusNotFound)
}

func GetParticipant(db *sql.DB, cache *redis.Client) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		key := "Participant::" + vars["id"]
		// Since we write to the ResponseWriter directly,
		// we saved []byte into the cache.
		val, err := cache.Get(key).Bytes()

		if err == redis.Nil {
			GetParticipantFromDB(vars["id"], db, cache, w)
			return
		} else if err != nil {
			LogError(w, err, "cannot fetch participant from cache: %s", http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json; charset=UTF-8")
		w.Write(val)
	})
}
