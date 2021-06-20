package edge

import (
     "encoding/json"
     "fmt"
     "io"
     "log"
     "time"
     "errors"
     "regexp"
     "net/http"
     "github.com/graphql-go/graphql"
)

var idPattern = regexp.MustCompile(`id:"[0-9]*"`)

type Friend struct {

        Id int64 `json:"id,omitempty"`

        From string `json:"from,omitempty"`

        To string `json:"to,omitempty"`
}

func getFriends(params graphql.ResolveParams) (interface{}, error) {
     idQuery, isOK := params.Args["id"].(string)
     if isOK {
        resp, err := http.Get("http://feed:8080/participant/" + idQuery + "/friends")
        if err != nil {
           log.Printf("user: %s, cannot get friends: %s", idQuery, err)
           return 0, errors.New("cannot get friends")
        }
        defer resp.Body.Close()
        body, err := io.ReadAll(resp.Body)
        if err != nil {
           log.Printf("user: %s, cannot read friends response: %s", idQuery, err)
           return 0, errors.New("cannot read friends response")
        }
        var fs []Friend
        err = json.Unmarshal([]byte(string(body)), &fs)
        if err != nil {
           return "", errors.New("get friends invalid response")
        }
        pidpath := "/participant/" + idQuery
        rv := make([]Participant, len(fs), cap(fs))
        for i, f := range fs {
            fidpath := f.To
            if fidpath == pidpath {
               fidpath = f.From
            }
            p, err := getParticipantInner(fidpath)
            if err == nil {
               rv[i] = p
            }
        }
        return rv, nil
     }
     return nil, errors.New("participant id not specified")
}

func getParticipantInner(path string) (Participant, error) {
     var rv Participant
     resp, err := http.Get("http://feed:8080" + path)
     if err != nil {
        log.Printf("user: %s, cannot get participant: %s", path, err)
        return rv, errors.New("cannot get participant")
     }
     defer resp.Body.Close()
     body, err := io.ReadAll(resp.Body)
     if err != nil {
        log.Printf("participant: %s, cannot read response: %s", path, err)
        return rv, errors.New("cannot read participant response")
     }
     err = json.Unmarshal([]byte(string(body)), &rv)
     if err != nil {
        return rv, errors.New("get participant invalid response")
     }
     return rv, nil
}

func getParticipant(params graphql.ResolveParams) (interface{}, error) {
     idQuery, isOK := params.Args["id"].(string)
     if isOK {
        return getParticipantInner("/participant/" + idQuery)
     }
     return nil, errors.New("participant id not specified")
}

type InboundInner struct {

        From string `json:"from"`

        Occurred time.Time `json:"occurred,omitempty"`

        Subject string `json:"subject,omitempty"`

        Story string `json:"story,omitempty"`
}

func getInbound(params graphql.ResolveParams) (interface{}, error) {
     idQuery, isOK := params.Args["id"].(string)
     if isOK {
        resp, err := http.Get("http://feed:8080/participant/" + idQuery + "/inbound")
        if err != nil {
           log.Printf("user: %s, cannot get inbound: %s", idQuery, err)
           return 0, errors.New("cannot get inbound")
        }
        defer resp.Body.Close()
        body, err := io.ReadAll(resp.Body)
        if err != nil {
           log.Printf("user: %s, cannot read inbound response: %s", idQuery, err)
           return 0, errors.New("cannot read inbound response")
        }
        var ii []InboundInner
        err = json.Unmarshal([]byte(string(body)), &ii)
        if err != nil {
           return "", errors.New("get inbound invalid response")
        }
        rv := make([]Inbound, len(ii), cap(ii))
        for j, i := range ii {
            p, err := getParticipantInner(i.From)
            if err == nil {
               in := Inbound{
                  From: p,
                  Occurred: i.Occurred,
                  Subject: i.Subject,
                  Story: i.Story,
               }
               rv[j] = in
            }
        }
        return rv, nil
     }
     return nil, errors.New("participant id not specified")
}
func getOutbound(params graphql.ResolveParams) (interface{}, error) {
     idQuery, isOK := params.Args["id"].(string)
     if isOK {
        resp, err := http.Get("http://feed:8080/participant/" + idQuery + "/outbound")
        if err != nil {
           log.Printf("user: %s, cannot get outbound: %s", idQuery, err)
           return 0, errors.New("cannot get outbound")
        }
        defer resp.Body.Close()
        body, err := io.ReadAll(resp.Body)
        if err != nil {
           log.Printf("user: %s, cannot read outbound response: %s", idQuery, err)
           return 0, errors.New("cannot read outbound response")
        }
        var o []Outbound
        err = json.Unmarshal([]byte(string(body)), &o)
        if err != nil {
           return "", errors.New("get outbound invalid response")
        }
        return o, nil
     }
     return nil, errors.New("participant id not specified")
}
func ExecuteQuery(w http.ResponseWriter, r *http.Request) {
     token, err := srv.ValidationBearerToken(r)
     if err != nil {
        http.Error(w, err.Error(), http.StatusForbidden)
        return
     }
     userId := token.GetUserID()
     requestQuery := r.URL.Query().Get("query")
     payload := fmt.Sprintf("id:\"%s\"", userId)
     finalQuery := idPattern.ReplaceAllLiteralString(requestQuery, payload)
     result := graphql.Do(graphql.Params{
        Schema:        NewsFeedSchema,
        RequestString: finalQuery,
     })
     if len(result.Errors) > 0 {
        diag := fmt.Sprintf("wrong result, unexpected errors: %v", result.Errors)
        log.Printf(finalQuery)
        log.Printf(diag)
        http.Error(w, diag, http.StatusBadRequest)
        return
     }
     json.NewEncoder(w).Encode(result)
}

