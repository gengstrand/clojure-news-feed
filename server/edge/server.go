package main

import (
     "encoding/json"
     "flag"
     "fmt"
     "bytes"
     "io"
     "log"
     "errors"
     "net/http"
     "net/http/httputil"
     "net/url"
     "os"
     "time"
     "context"
     "github.com/google/uuid"
     "github.com/gorilla/websocket"
     "github.com/go-oauth2/oauth2/generates"
     "github.com/go-oauth2/oauth2/manage"
     "github.com/go-oauth2/oauth2/models"
     "github.com/go-oauth2/oauth2/server"
     "github.com/go-oauth2/oauth2/store"
     "github.com/go-redis/redis"
     "github.com/go-session/session"
)

var (
     dumpvar      bool
     domainvar    string
     portvar      int
     pollvar	  int
     clientStore  *store.ClientStore
     upgrader	  websocket.Upgrader
     credDB       *redis.Client
     srv	  *server.Server
     ctx          context.Context
     ttl          time.Duration
)

func init() {
     flag.BoolVar(&dumpvar, "d", true, "Dump requests and responses")
     flag.StringVar(&domainvar, "r", "http://localhost:3000", "The domain of the redirect url")
     flag.IntVar(&portvar, "p", 8080, "the base port for the server")
     flag.IntVar(&pollvar, "p", 10, "seconds between polling inbound") 
     credDB = redis.NewClient(&redis.Options {
          Addr: "redis:6379",
          DB: 1,
     })
     ctx = context.Background()
     clientStore = store.NewClientStore()
     upgrader = websocket.Upgrader{}
     ttl = time.Hour
}

func main() {
     flag.Parse()
     if dumpvar {
        log.Println("Dumping requests")
     }
     manager := manage.NewDefaultManager()
     
     manager.SetAuthorizeCodeTokenCfg(manage.DefaultAuthorizeCodeTokenCfg)
     manager.MustTokenStorage(store.NewMemoryTokenStore())

     // generate jwt access token
     // manager.MapAccessGenerate(generates.NewJWTAccessGenerate("", []byte("00000000"), jwt.SigningMethodHS512))
     manager.MapAccessGenerate(generates.NewAccessGenerate())

     manager.MapClientStorage(clientStore)

     srv = server.NewServer(server.NewConfig(), manager)

     srv.SetPasswordAuthorizationHandler(validateCredentialsHandler)

     srv.SetUserAuthorizationHandler(userAuthorizeHandler)

     http.HandleFunc("/login", loginHandler)
     http.HandleFunc("/auth", authHandler)
     http.HandleFunc("/outbound/stream", streamOutboundHandler)

     http.HandleFunc("/oauth/authorize", func(w http.ResponseWriter, r *http.Request) {
          if dumpvar {
               dumpRequest(os.Stdout, "authorize", r)
          }

          store, err := session.Start(r.Context(), w, r)
          if err != nil {
               http.Error(w, err.Error(), http.StatusInternalServerError)
               return
          }

          var form url.Values
          if v, ok := store.Get("ReturnUri"); ok {
               form = v.(url.Values)
          }
          r.Form = form

          store.Delete("ReturnUri")
          store.Save()

          err = srv.HandleAuthorizeRequest(w, r)
          if err != nil {
               http.Error(w, err.Error(), http.StatusBadRequest)
          }
     })

     http.HandleFunc("/oauth/token", func(w http.ResponseWriter, r *http.Request) {
          if dumpvar {
               _ = dumpRequest(os.Stdout, "token", r) // Ignore the error
          }

          err := srv.HandleTokenRequest(w, r)
          if err != nil {
               http.Error(w, err.Error(), http.StatusInternalServerError)
          }
     })

     http.HandleFunc("/test", func(w http.ResponseWriter, r *http.Request) {
          if dumpvar {
               _ = dumpRequest(os.Stdout, "test", r) // Ignore the error
          }
          token, err := srv.ValidationBearerToken(r)
          if err != nil {
               http.Error(w, err.Error(), http.StatusBadRequest)
               return
          }

          data := map[string]interface{}{
               "expires_in": int64(token.GetAccessCreateAt().Add(token.GetAccessExpiresIn()).Sub(time.Now()).Seconds()),
               "client_id":  token.GetClientID(),
               "user_id":    token.GetUserID(),
          }
          e := json.NewEncoder(w)
          e.SetIndent("", "  ")
          e.Encode(data)
     })

     log.Printf("Server is running at %d port.\n", portvar)
     log.Printf("Point your OAuth client Auth endpoint to %s:%d%s", "http://localhost", portvar, "/oauth/authorize")
     log.Printf("Point your OAuth client Token endpoint to %s:%d%s", "http://localhost", portvar, "/oauth/token")
     log.Fatal(http.ListenAndServe(fmt.Sprintf(":%d", portvar), nil))
}

type FeedCredentials struct {
     Password string     `json:"password"`
     UserId string       `json:"userid"`
}

type Participant struct {
     Id string           `json:"id,omitempty"`
     Name string         `json:"name,omitempty"`
     Link string         `json:"link,omitempty"`
}

func validateCredentialsHandler(username, password string) (userID string, err error) {
     var fc FeedCredentials
     c, err := credDB.Get(username).Result()
     switch {
     case err == redis.Nil:
       ip := Participant{"", username, ""}
       b, err := json.Marshal(ip)
       if err != nil {
          return "", errors.New("username not well founded")
       }
       resp, err := http.Post("http://feed:8080/participant", "application/json", bytes.NewReader(b))
       if err != nil {
          return "", errors.New("cannot create participant")
       }
       var p Participant
       body, err := io.ReadAll(resp.Body)
       if err != nil {
          return "", errors.New("cannot fetch response from create participant call")
       }
       err = json.Unmarshal([]byte(string(body)), &p)
       if err != nil {
          return "", errors.New("create participant invalid response")
       }
       fc = FeedCredentials {password, p.Id}
       bb, err := json.Marshal(fc)
       if err != nil {
          return "", errors.New("password has illegal characters")
       }
       credDB.Set(username, bb, ttl)
       u, err := uuid.NewRandom()
       if err != nil {
          return "", errors.New("cannot generate secret")
       }
       secret := fmt.Sprintf("%s", u)
       clientStore.Set(p.Id, &models.Client{
         ID:     p.Id,
         Secret: secret,
         Domain: domainvar,
       })
       return p.Id, nil
     case err == nil:
       return "", errors.New("cannot connect to redis")
     case c == "":
       return "", errors.New("invalid credentials format")
     default:
       err = json.Unmarshal([]byte(c), &fc)
       if err != nil {
          return "", errors.New("invalid credentials schema")
       } else {
         if fc.Password == password {
            return fc.UserId, nil
         } else {
            return "", errors.New("incorrect password")
         }
       }
     }
}

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

func streamOutboundHandler(w http.ResponseWriter, r *http.Request) {
     token, err := srv.ValidationBearerToken(r)
     if err != nil {
        http.Error(w, err.Error(), http.StatusForbidden)
        return
     }
     userId := token.GetUserID()
     c, err := upgrader.Upgrade(w, r, nil)
     if err != nil {
        log.Printf("user: %s, upgrade: %s", userId, err)
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
     }
     defer c.Close()
     mt, message, err := c.ReadMessage()
     if err != nil {
        log.Printf("user: %s, read: %s", userId, err)
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
     }
     if dumpvar {
     	log.Printf("user: %s, recv: %s", userId, message)
     }
     inboundLength := 0
     for {
     	time.Sleep(time.Duration(pollvar) * time.Millisecond)
	il, err := getInboundLength(userId)
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

func dumpRequest(writer io.Writer, header string, r *http.Request) error {
     data, err := httputil.DumpRequest(r, true)
     if err != nil {
          return err
     }
     writer.Write([]byte("\n" + header + ": \n"))
     writer.Write(data)
     return nil
}

func userAuthorizeHandler(w http.ResponseWriter, r *http.Request) (userID string, err error) {
     if dumpvar {
          _ = dumpRequest(os.Stdout, "userAuthorizeHandler", r) // Ignore the error
     }
     store, err := session.Start(r.Context(), w, r)
     if err != nil {
          return
     }

     uid, ok := store.Get("LoggedInUserID")
     if !ok {
          if r.Form == nil {
               r.ParseForm()
          }

          store.Set("ReturnUri", r.Form)
          store.Save()

          w.Header().Set("Location", "/login")
          w.WriteHeader(http.StatusFound)
          return
     }

     userID = uid.(string)
     store.Delete("LoggedInUserID")
     store.Save()
     return
}

func loginHandler(w http.ResponseWriter, r *http.Request) {
     if dumpvar {
          _ = dumpRequest(os.Stdout, "login", r) // Ignore the error
     }
     store, err := session.Start(r.Context(), w, r)
     if err != nil {
          http.Error(w, err.Error(), http.StatusInternalServerError)
          return
     }

     if r.Method == "POST" {
          if r.Form == nil {
               if err := r.ParseForm(); err != nil {
                    http.Error(w, err.Error(), http.StatusInternalServerError)
                    return
               }
          }
          store.Set("LoggedInUserID", r.Form.Get("username"))
          store.Save()

          w.Header().Set("Location", "/auth")
          w.WriteHeader(http.StatusFound)
          return
     }
     outputHTML(w, r, "static/login.html")
}

func authHandler(w http.ResponseWriter, r *http.Request) {
     if dumpvar {
          _ = dumpRequest(os.Stdout, "auth", r) // Ignore the error
     }
     store, err := session.Start(nil, w, r)
     if err != nil {
          http.Error(w, err.Error(), http.StatusInternalServerError)
          return
     }

     if _, ok := store.Get("LoggedInUserID"); !ok {
          w.Header().Set("Location", "/login")
          w.WriteHeader(http.StatusFound)
          return
     }

     outputHTML(w, r, "static/auth.html")
}

func outputHTML(w http.ResponseWriter, req *http.Request, filename string) {
     file, err := os.Open(filename)
     if err != nil {
          http.Error(w, err.Error(), 500)
          return
     }
     defer file.Close()
     fi, _ := file.Stat()
     http.ServeContent(w, req, file.Name(), fi.ModTime(), file)
}
