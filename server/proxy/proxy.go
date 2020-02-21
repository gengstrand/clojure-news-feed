package main 

import (
    "io"
    "log"
    "fmt"
    "time"
    "bytes"
    "regexp"
    "strings"
    "net/http"
    "io/ioutil"
    "encoding/json"
    "github.com/prometheus/client_golang/prometheus"
    "github.com/prometheus/client_golang/prometheus/promhttp"
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

func isGraphQl(path string, method string) bool {
    if &path == nil || strings.Compare("/", path) == 0 {
       if &method == nil || strings.Compare(strings.ToUpper(method), "POST") == 0 {
          return true
       }
    }
    return false
}

var graphQlMatcher = regexp.MustCompile(`mutation.*create([A-Z][a-z]+)\(`)
var restMatcher = regexp.MustCompile(`/participant/([0-9]+)/([a-z]+)`)
var metricsHandler = promhttp.Handler()
var metrics = make(map[string]prometheus.Histogram)
var myBuckets = prometheus.LinearBuckets(0.0, 50.0, 6)
func logToPrometheus(entity string, operation string, status int, duration int64) {
    name := fmt.Sprintf("%s_%s_%d", entity, operation, status)
    _, found := metrics[name]
    if !found {
       metrics[name] = prometheus.NewHistogram(prometheus.HistogramOpts{Name: name, Buckets: myBuckets})
       prometheus.MustRegister(metrics[name])
    }
    metric := metrics[name]
    metric.Observe(float64(duration))
}

func makePerfLogEntry(path string, method string, body string, status int, duration int64) HttpLog {
    var req HttpLogRequest
    if isGraphQl(path, method) {
       m := graphQlMatcher.FindStringSubmatch(body)
       if &m != nil && len(m) > 1 {
          o := strings.ToLower(m[1])
          logToPrometheus(o, method, status, duration)
          req = HttpLogRequest{fmt.Sprintf("/%s/new", o), method}
       }
    } else {
       if strings.Compare("get", strings.ToLower(method)) == 0 && strings.Compare("/outbound", strings.ToLower(path)) == 0 {
         logToPrometheus("search", "POST", status, duration)
         req = HttpLogRequest{"/outbound/search", "POST"}
       } else {
         m := restMatcher.FindStringSubmatch(path)
         if &m != nil && len(m) > 2 {
	   o := strings.ToLower(m[2])
           if strings.Compare("post", strings.ToLower(method)) == 0 {
       	     req = HttpLogRequest{fmt.Sprintf("/%s/new", o), method}
	   } else {
       	     req = HttpLogRequest{fmt.Sprintf("/%s/%s", o, m[1]), method}
	   }
	   logToPrometheus(o, method, status, duration)
         } else {
	   logToPrometheus("participant", method, status, duration)
       	   req = HttpLogRequest{path, method}
         }
       }
    }
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
    var body string 
    if isGraphQl(req.URL.Path, req.Method) {
       b, _ := ioutil.ReadAll(req.Body)
       body = string(b)
       req.Body = ioutil.NopCloser(bytes.NewBuffer(b))
    }
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
    activity <- makePerfLogEntry(req.URL.Path, req.Method, body, resp.StatusCode, int64(after.Sub(before)) / 1000000)
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
	    if strings.Compare("/metrics", strings.ToLower(r.URL.Path)) == 0 {
	       metricsHandler.ServeHTTP(w, r)
	    } else {
               handleHTTP(w, r)
	    }
        }),
    }
    for i:=0;i<3;i++ {
        go handlePerfLog()
    }
    log.Fatal(server.ListenAndServe())
}

