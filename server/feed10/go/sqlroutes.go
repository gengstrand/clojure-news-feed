package newsfeedserver

import (
	"database/sql"
	"log"
	"net/http"

	"github.com/gorilla/mux"
)

// An sqlRoute needs access to the database.
// So we compose a type consisting of all the properties
// of basicRoute and add the specific HandlerFunc,
// which needs to get a *sql.DB handed over.
// See the routes which are set up as sqlRoutes for details.
type sqlRoute struct {
	basicRoute
	HandlerFunc func(*sql.DB) http.Handler
}

type sqlRoutes []sqlRoute

func (routes sqlRoutes) setup(router *mux.Router, db *sql.DB) {
	for _, route := range routes {
		log.Printf("Setting up %s with path '%s'", route.Name, route.Pattern)

		router.
			Methods(route.Method).
			Path(route.Pattern).
			Name(route.Name).
			Handler(
				Logger(
					route.HandlerFunc(db), route.Name,
				),
			)
	}
}

var sqlroutesList = sqlRoutes{

	sqlRoute{
		basicRoute{"AddParticipant",
			http.MethodPost,
			"/participant/new"},
		AddParticipant,
	},
}
