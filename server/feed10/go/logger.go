package newsfeedserver

import (
    "log"
    "fmt"
    "net/http"
    "time"
)

func LogError(w http.ResponseWriter, err error, format string, status int) {
     msg := fmt.Sprintf(format, err)
     log.Println(msg)
     http.Error(w, msg, status)
}

func Logger(inner http.Handler, name string) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()

        inner.ServeHTTP(w, r)

        log.Printf(
            "%s %s %s %s",
            r.Method,
            r.RequestURI,
            name,
            time.Since(start),
        )
    })
}
