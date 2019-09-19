package newsfeedserver

import (
	"log"
	"net/http"

	"github.com/gorilla/mux"
)

// We compose our simpleRoute type
// of all the properties in basicRoute
// and add the HandlerFunc property
// which is specific to this type of route.
//
// Since this type will not be used outside
// this application, there is no need to export it.
type simpleRoute struct {
	basicRoute
	HandlerFunc func(http.ResponseWriter, *http.Request)
}

type simpleRoutes []simpleRoute

// Our simpleRoutes type has a function,
// namely the setup.
// It iterates over all elements of its receiver
// and adds the according routes to router.
// Note: simpleRoutes is a type distinctive from and NOT
// equivalent to []simpleRoute. Hence, this nifty "trick"
// would not hav unexpected side effects on a []simpleRoute.
func (routes simpleRoutes) setup(router *mux.Router) {
	for _, route := range routes {
		log.Printf("Setting up %s with path '%s'", route.Name, route.Pattern)
		router.
			Methods(route.Method).
			Path(route.Pattern).
			Name(route.Name).
			Handler(
				Logger(
					http.HandlerFunc(route.HandlerFunc), route.Name,
				),
			)
	}
}

var simpleRoutesList = simpleRoutes{
	simpleRoute{
		basicRoute{
			"Index",
			http.MethodGet,
			"/",
		},
		Index,
	},
}

// Index redirects to the owner's personal page.
func Index(w http.ResponseWriter, r *http.Request) {
	http.Redirect(w, r, "http://glennengstrand.info", http.StatusSeeOther)
}
