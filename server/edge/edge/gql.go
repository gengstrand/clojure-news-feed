package edge

import (
     "encoding/json"
     "fmt"
     "io"
     "log"
     "errors"
     "net/http"
     "github.com/graphql-go/graphql"
)

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
        var f []Friend
        err = json.Unmarshal([]byte(string(body)), &f)
        if err != nil {
           return "", errors.New("get friends invalid response")
        }
        return f, nil
     }
     return nil, errors.New("participant id not specified")
}

func getParticipant(params graphql.ResolveParams) (interface{}, error) {
     idQuery, isOK := params.Args["id"].(string)
     if isOK {
        resp, err := http.Get("http://feed:8080/participant/" + idQuery)
        if err != nil {
           log.Printf("user: %s, cannot get participant: %s", idQuery, err)
           return 0, errors.New("cannot get participant")
        }
        defer resp.Body.Close()
        body, err := io.ReadAll(resp.Body)
        if err != nil {
           log.Printf("user: %s, cannot read participant response: %s", idQuery, err)
           return 0, errors.New("cannot read participant response")
        }
        var p Participant
        err = json.Unmarshal([]byte(string(body)), &p)
        if err != nil {
           return "", errors.New("get participant invalid response")
        }
        return p, nil
     }
     return nil, errors.New("participant id not specified")
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
        var i []Inbound
        err = json.Unmarshal([]byte(string(body)), &i)
        if err != nil {
           return "", errors.New("get inbound invalid response")
        }
        return i, nil
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
     data := r.URL.Query().Get("data")
     query := fmt.Sprintf("{participant(id:\"%s\")%s}", userId, data)
     result := graphql.Do(graphql.Params{
        Schema:        NewsFeedSchema,
        RequestString: query,
     })
     if len(result.Errors) > 0 {
        diag := fmt.Sprintf("wrong result, unexpected errors: %v", result.Errors)
        log.Printf(query)
        log.Printf(diag)
        http.Error(w, diag, http.StatusBadRequest)
        return
     }
     json.NewEncoder(w).Encode(result)
}

