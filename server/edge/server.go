package main

import (
     "flag"
     "log"
     "fmt"
     "net/http"
     "proxy"
)

var portvar int

func init() {
     flag.BoolVar(&edge.Dumpvar, "d", true, "Dump requests and responses")
     flag.StringVar(&edge.Domainvar, "r", "http://localhost:3000", "The domain of the redirect url")
     flag.IntVar(&portvar, "p", 8080, "the base port for the server")
     flag.IntVar(&edge.Pollvar, "p", 10, "seconds between polling inbound") 
}

func main() {
     flag.Parse()
     if edge.Dumpvar {
        log.Println("Dumping requests")
     }
     http.HandleFunc("/login", edge.LoginHandler)
     http.HandleFunc("/auth", edge.AuthHandler)
     http.HandleFunc("/oauth/authorize", edge.AuthorizeHandler)
     http.HandleFunc("/oauth/token", edge.TokenHandler)
     http.HandleFunc("/test", edge.TestHandler)
     http.HandleFunc("/inbound/stream", edge.StreamInboundHandler)
     http.HandleFunc("/graphql", edge.ExecuteQuery)
     log.Printf("Server is running at %d port.\n", portvar)
     log.Printf("Point your OAuth client Auth endpoint to %s:%d%s", "http://localhost", portvar, "/oauth/authorize")
     log.Printf("Point your OAuth client Token endpoint to %s:%d%s", "http://localhost", portvar, "/oauth/token")
     log.Fatal(http.ListenAndServe(fmt.Sprintf(":%d", portvar), nil))
}

