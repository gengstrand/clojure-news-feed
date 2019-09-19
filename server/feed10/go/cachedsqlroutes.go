package newsfeedserver

import (
	"database/sql"
	"log"
	"net/http"

	"github.com/go-redis/redis"
	"github.com/gorilla/mux"
)

type cachedSQLRoute struct {
	basicRoute
	HandlerFunc func(*sql.DB, *redis.Client) http.Handler
}

type cachedSQLRoutes []cachedSQLRoute

func (routes cachedSQLRoutes) setup(router *mux.Router, db *sql.DB, cache *redis.Client) {
	for _, route := range routes {
		log.Printf("Setting up %s with path '%s'", route.Name, route.Pattern)
		router.
			Methods(route.Method).
			Path(route.Pattern).
			Name(route.Name).
			Handler(
				Logger(
					route.HandlerFunc(db, cache), route.Name,
				),
			)
	}
}

var cachedSQLRoutesList = cachedSQLRoutes{
	cachedSQLRoute{
		basicRoute{
			"GetParticipant",
			http.MethodGet,
			"/participant/{id}",
		},
		GetParticipant,
	},
	cachedSQLRoute{
		basicRoute{"GetFriend",
			http.MethodGet,
			"/friends/{id}"},
		GetFriend,
	},
	cachedSQLRoute{
		basicRoute{"AddFriend",
			http.MethodPost,
			"/friends/new"},
		AddFriend,
	},
}
