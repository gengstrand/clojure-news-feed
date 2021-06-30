package edge

import (
     "io"
     "log"
     "errors"
     "strconv"
     "net/http"
     "time"
     "github.com/gorilla/websocket"
)

var (
    upgrader = websocket.Upgrader{}
    Pollvar int
)

func getInboundLength(userId string) (bodyLen int, err error) {
     resp, err := http.Get("http://feed:8080/participant/" + userId + "/inbound")
     if err != nil {
        log.Printf("user: %s, cannot get inbound: %s", userId, err)
        return 0, errors.New("cannot get inbound")
     }
     defer resp.Body.Close()
     body, err := io.ReadAll(resp.Body)
     if err != nil {
        log.Printf("user: %s, cannot read inbound response: %s", userId, err)
        return 0, errors.New("cannot read inbound response")
     }
     return len(body), nil
}

func StreamInboundHandler(w http.ResponseWriter, r *http.Request) {
     c, err := upgrader.Upgrade(w, r, nil)
     if err != nil {
        log.Printf("upgrade: %s", err)
        return
     }
     defer c.Close()
     mt, message, err := c.ReadMessage()
     if err != nil {
        log.Printf("read: %s", err)
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
     }
     if Dumpvar {
        log.Printf("user: %s", message)
     }
     uid := string(message)
     userId, err := strconv.Atoi(uid)
     if err != nil {
        log.Printf("expected user id but got %s", message)
        http.Error(w, err.Error(), http.StatusBadRequest)
        return
     }
     inboundLength := 0
     for {
        time.Sleep(time.Duration(Pollvar) * time.Millisecond)
        il, err := getInboundLength(uid)
        if err != nil {
           log.Printf("user: %s, get inbound: %s", userId, err)
           http.Error(w, err.Error(), http.StatusInternalServerError)
           return
        }
        if il > inboundLength {
           inboundLength = il
           err = c.WriteMessage(mt, []byte("changed"))
        } else {
           err = c.WriteMessage(mt, []byte("same"))
        }
        if err != nil {
           log.Printf("user: %s, write: %s", userId, err)
           http.Error(w, err.Error(), http.StatusInternalServerError)
           return
        }
     }
}

