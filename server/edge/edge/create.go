package edge

import (
     "io"
     "log"
     "fmt"
     "bytes"
     "encoding/json"
     "net/http"
)

func CreateOutboundHandler(w http.ResponseWriter, r *http.Request) {
     token, err := srv.ValidationBearerToken(r)
     if err != nil {
        http.Error(w, err.Error(), http.StatusForbidden)
        return
     }
     userId := token.GetUserID()
     url := fmt.Sprintf("http://feed:8080/participant/%s/outbound", userId)
     defer r.Body.Close()
     body, err := io.ReadAll(r.Body)
     if err != nil {
        log.Printf("user: %s, cannot read request body: %s", userId, err)
        http.Error(w, err.Error(), http.StatusBadRequest)
     }
     resp, err := http.Post(url, "application/json", bytes.NewReader(body))
     if err != nil {
        log.Printf("cannot create outbound")
        http.Error(w, err.Error(), http.StatusInternalServerError)
     }
     defer resp.Body.Close()
     responseb, err := io.ReadAll(resp.Body)
     if err != nil {
        log.Printf("user: %s, cannot read create outbound response: %s", userId, err)
        http.Error(w, err.Error(), http.StatusInternalServerError)
     }
     w.Header().Set("Content-Type", "application/json; charset=UTF-8")
     fmt.Fprint(w, string(responseb))
     w.WriteHeader(resp.StatusCode)
}

func CreateFriendHandler(w http.ResponseWriter, r *http.Request) {
     token, err := srv.ValidationBearerToken(r)
     if err != nil {
        http.Error(w, err.Error(), http.StatusForbidden)
        return
     }
     userId := token.GetUserID()
     url := fmt.Sprintf("http://feed:8080/participant/%s/friends", userId)
     defer r.Body.Close()
     body, err := io.ReadAll(r.Body)
     if err != nil {
        log.Printf("user: %s, cannot read request body: %s", userId, err)
        http.Error(w, err.Error(), http.StatusBadRequest)
     }
     var rbf Friend
     err = json.Unmarshal([]byte(string(body)), &rbf)
     if err != nil {
        log.Printf("user: %s, invalid friend request body: %s", userId, err)
        http.Error(w, err.Error(), http.StatusBadRequest)        
     }
     f := Friend{
       From: "/participant/" + userId,
       To: rbf.To,
     }
     b, err := json.Marshal(f)
     if err != nil {
        log.Printf("cannot prepare create friend request as json")
        http.Error(w, err.Error(), http.StatusBadRequest)
     }
     resp, err := http.Post(url, "application/json", bytes.NewReader(b))
     if err != nil {
        log.Printf("cannot create outbound")
        http.Error(w, err.Error(), http.StatusInternalServerError)
     }
     defer resp.Body.Close()
     responseb, err := io.ReadAll(resp.Body)
     if err != nil {
        log.Printf("user: %s, cannot read create friend response: %s", userId, err)
        http.Error(w, err.Error(), http.StatusInternalServerError)
     }
     w.Header().Set("Content-Type", "application/json; charset=UTF-8")
     fmt.Fprint(w, string(responseb))
     w.WriteHeader(resp.StatusCode)
}
