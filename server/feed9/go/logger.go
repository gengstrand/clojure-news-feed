package newsfeedserver

import (
    "log"
    "fmt"
    "net/http"
    "time"
)

type ErrorWrapper interface {
     LogError(err error, format string, status int)
}

type LogWrapper struct {
     Writer http.ResponseWriter
}

func (lw LogWrapper) LogError(err error, format string, status int) {
     msg := fmt.Sprintf(format, err)
     log.Println(msg)
     http.Error(lw.Writer, msg, status)
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
