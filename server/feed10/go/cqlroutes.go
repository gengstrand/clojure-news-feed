package newsfeedserver

import (
	"log"
	"net/http"

	"github.com/gocql/gocql"
	"github.com/gorilla/mux"
)

// https://github.com/gocql/gocql/issues/1048#issuecomment-357195540
type cqlRoute struct {
	basicRoute
	HandlerFunc func(*gocql.Session) http.Handler
}

type cqlRoutes []cqlRoute

func (routes cqlRoutes) setup(router *mux.Router, session *gocql.Session) {
	for _, route := range routes {
		log.Printf("Setting up %s with path '%s'", route.Name, route.Pattern)

		router.
			Methods(route.Method).
			Path(route.Pattern).
			Name(route.Name).
			Handler(
				Logger(
					route.HandlerFunc(session), route.Name,
				),
			)
	}
}

var cqlRoutesList = cqlRoutes{
	cqlRoute{
		basicRoute{
			"GetInbound",
			http.MethodGet,
			"/inbound/{id}"},
		GetInbound,
	},
	cqlRoute{
		basicRoute{"GetOutbound",
			http.MethodGet,
			"/outbound/{id}"},
		GetOutbound,
	},
}
