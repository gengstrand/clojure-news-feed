package newsfeedserver

import (
	"database/sql"
	"log"
	"net/http"

	"github.com/go-redis/redis"
	"github.com/gocql/gocql"
	"github.com/gorilla/mux"
)

type cqlCachedSQLRoute struct {
	basicRoute
	HandlerFunc func(*gocql.Session, *sql.DB, *redis.Client) http.Handler
}

type cqlCachedSQLRoutes []cqlCachedSQLRoute

func (routes cqlCachedSQLRoutes) setup(router *mux.Router, session *gocql.Session, db *sql.DB, cache *redis.Client) {
	for _, route := range routes {
		log.Printf("Setting up %s with path '%s'", route.Name, route.Pattern)
		router.
			Methods(route.Method).
			Path(route.Pattern).
			Name(route.Name).
			Handler(
				Logger(
					route.HandlerFunc(session, db, cache), route.Name,
				),
			)
	}
}

var cqlCachedSQLRoutesList = cqlCachedSQLRoutes{
	cqlCachedSQLRoute{
		basicRoute{"AddOutbound",
			http.MethodPost,
			"/outbound/new"},
		AddOutbound,
	},
}
