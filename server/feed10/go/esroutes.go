package newsfeedserver

import (
	"log"
	"net/http"
	"sync"

	"github.com/gorilla/mux"
)

type esRoute struct {
	basicRoute
	HandlerFunc func(*sync.Pool) http.Handler
}

type esRoutes []esRoute

func (routes esRoutes) setup(router *mux.Router, pool *sync.Pool) {
	for _, route := range routes {
		log.Printf("Setting up %s with path '%s'", route.Name, route.Pattern)

		router.
			Methods(route.Method).
			Path(route.Pattern).
			Name(route.Name).
			Handler(
				Logger(
					route.HandlerFunc(pool), route.Name,
				),
			)
	}
}

var esRoutesList = esRoutes{
	esRoute{
		basicRoute{"SearchOutbound",
			http.MethodPost,
			"/outbound/search"},
		SearchOutbound,
	},
}
