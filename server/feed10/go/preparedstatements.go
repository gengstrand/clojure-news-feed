package newsfeedserver

import (
	"database/sql"
	"log"
	"os"
	"os/signal"
	"syscall"
)

// UpsertStmt is the prepared statement
//  call UpsertParticipant(?)
var UpsertStmt *sql.Stmt

// FetchParticipantStmt is the prepated statement
//  call FetchParticipant(?)
var FetchParticipantStmt *sql.Stmt

// UpsertFriendsStmt is the prepared statement
//  call UpsertFriends(?, ?)
var UpsertFriendsStmt *sql.Stmt

// FetchFriendsStmt is the prepared statement
//  call FetchFriends(?)
var FetchFriendsStmt *sql.Stmt

func init() {

	// We should close the prepared statements.
	// Due to the project layout and some of the contraints of Go
	// interacting, the easiest way is to listen for a syscall
	// terminating the program.
	// See https://gobyexample.com/signals for details.
	sigs := make(chan os.Signal, 1)

	signal.Notify(sigs, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		<-sigs
		log.Println("Releasing prepared statements on the server")
		stmts := []*sql.Stmt{
			UpsertStmt,
			FetchParticipantStmt,
			UpsertFriendsStmt,
			FetchFriendsStmt}

		for _, stmt := range stmts {
			if stmt != nil {
				stmt.Close()
			}
		}

		log.Println("Released prepared statements on the server.")
	}()

}

func setupStatements(db *sql.DB) {
	var err error

	if UpsertStmt, err = db.Prepare("call UpsertParticipant(?)"); err != nil {
		log.Fatalf("cannot prepare UpsertStmt: %s", err)
	}

	if FetchParticipantStmt, err = db.Prepare("call FetchParticipant(?)"); err != nil {
		log.Fatalf("cannot prepare FetchParticipantStmt: %s", err)
	}

	if UpsertFriendsStmt, err = db.Prepare("call UpsertFriends(?, ?)"); err != nil {
		log.Fatalf("cannot prepare UpsertFriendsStmt: %s", err)
	}

	if FetchFriendsStmt, err = db.Prepare("call FetchFriends(?)"); err != nil {
		log.Fatalf("cannot prepare UpsertFriendsStmt: %s", err)
	}
}
