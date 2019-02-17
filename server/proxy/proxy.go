package main 

import (
    "io"
    "log"
    "net/http"
    "time"
    "bytes"
    "encoding/json"
    "io/ioutil"
)

type HttpLogRequest struct {
    Uri string                      `json:"uri"`
    Method string                   `json:"method"`
}

type HttpLogResponse struct {
    Status int                      `json:"status"`
}

type HttpLogLatencies struct {
    Request int64                   `json:"request"`
}

type HttpLog struct {
    Request HttpLogRequest          `json:"request"`
    Response HttpLogResponse        `json:"response"`
    Latencies HttpLogLatencies      `json:"latencies"`
}

func makePerfLogEntry(path string, method string, status int, duration int64) HttpLog {
    req := HttpLogRequest{path, method}
    resp := HttpLogResponse{status}
    lat := HttpLogLatencies{duration}
    return HttpLog{req, resp, lat}
}

var activity = make(chan HttpLog)

func handlePerfLog() {
    client := &http.Client{}
    client.Timeout = time.Second * 10
    for {
        msg := <- activity
        b, err := json.Marshal(msg)
        if err == nil {
            body := bytes.NewBuffer(b)
            req, err := http.NewRequest(http.MethodPut, "http://kong-logger:8888", body)
            if err == nil {
                resp, err := client.Do(req)
                if err == nil {
		    io.Copy(ioutil.Discard, resp.Body)
                    resp.Body.Close()
                } else {
                    log.Fatalf("encountered error when calling kong logger", err)
                }
            } else {
                log.Fatalf("cannot create request to kong logger", err)
            }
        } else {
            log.Fatalf("cannot marshal performance log %s", err)
        }
    }
}

func handleHTTP(w http.ResponseWriter, req *http.Request) {
    req.Host = "feed:8080"
    req.URL.Host = "feed:8080"
    req.URL.Scheme = "http"
    before := time.Now()
    resp, err := http.DefaultTransport.RoundTrip(req)
    after := time.Now()
    if err != nil {
        http.Error(w, err.Error(), http.StatusServiceUnavailable)
        return
    }
    activity <- makePerfLogEntry(req.URL.Path, req.Method, resp.StatusCode, int64(after.Sub(before)) / 1000000)
    defer resp.Body.Close()
    copyHeader(w.Header(), resp.Header)
    w.WriteHeader(resp.StatusCode)
    io.Copy(w, resp.Body)
}

func copyHeader(dst, src http.Header) {
    for k, vv := range src {
        for _, v := range vv {
            dst.Add(k, v)
        }
    }
}

func main() {
    defaultRoundTripper := http.DefaultTransport
    defaultTransportPointer, ok := defaultRoundTripper.(*http.Transport)
    if ok {
        defaultTransport := *defaultTransportPointer 
    	defaultTransport.MaxIdleConns = 100
    	defaultTransport.MaxIdleConnsPerHost = 50
    } else {
        log.Fatalf("defaultRoundTripper not an *http.Transport")
    }
    server := &http.Server{
        Addr: ":8000",
        Handler: http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
            handleHTTP(w, r)
        }),
    }
    for i:=0;i<3;i++ {
        go handlePerfLog()
    }
    log.Fatal(server.ListenAndServe())
}

