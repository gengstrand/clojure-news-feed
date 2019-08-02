package newsfeedserver

import (
	"os"
        "fmt"
	"log"
	"strconv"
	"database/sql"
	_ "github.com/go-sql-driver/mysql"
)

type MySqlWrapper struct {
     db *sql.DB
}

type GetSqlWrapper interface {
     Close()
     FetchFriends(id string)([]Friend, error)
}

func (dbw MySqlWrapper) Close() {
     dbw.db.Close()
}

func (dbw MySqlWrapper) FetchFriends(id string)([]Friend, error) {
	stmt, err := dbw.db.Prepare("call FetchFriends(?)")
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

func connectMysql() (MySqlWrapper, error) {
	dbhost := fmt.Sprintf("feed:feed1234@tcp(%s:3306)/feed", os.Getenv("MYSQL_HOST"))
	db, err := sql.Open("mysql", dbhost)
	if err != nil {
	   return new(MySqlWrapper), err
	}
	retVal := MySqlWrapper{
	       db: db,
	}
	return retVal, nil
}
