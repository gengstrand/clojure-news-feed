package newsfeedserver

import (
        "os"
        "fmt"
	"sync"
	"database/sql"
	_ "github.com/go-sql-driver/mysql"
)
var lastError error = nil
var sqlPool = &sync.Pool{
	New: func() interface{} {
	     dbhost := fmt.Sprintf("feed:feed1234@tcp(%s:3306)/feed", os.Getenv("MYSQL_HOST"))
	     db, err := sql.Open("mysql", dbhost)
	     if err != nil {
	     	lastError = err
	     }
	     return db
	},
}

func MySqlConnect() (*sql.DB, error) {
     lastError = nil
     retVal := sqlPool.Get().(*sql.DB)
     if lastError != nil {
     	return nil, lastError
     } else {
        return retVal, nil
     }
}

func MySqlDisconnect(db *sql.DB) {
     sqlPool.Put(db)
}
