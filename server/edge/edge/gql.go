package edge

import (
     "encoding/json"
     "fmt"
     "io"
     "log"
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

func isFriend(path string, friends []Friend) (bool) {
   for _, f := range friends {
      if f.From == path || f.To == path {
         return true
      }
   }
   return false
}

func getFriendsInner(path string) ([]Friend, error) {
   resp, err := http.Get("http://feed:8080" + path + "/friends")
   if err != nil {
      return nil, errors.New("cannot get friends")
   }
   defer resp.Body.Close()
   body, err := io.ReadAll(resp.Body)
   if err != nil {
      return nil, errors.New("cannot read friends response")
   }
   var rv []Friend
   err = json.Unmarshal([]byte(string(body)), &rv)
   if err != nil {
      return nil, errors.New("get friends invalid response")
   }
   return rv, nil
}

func getFriends(params graphql.ResolveParams) (interface{}, error) {
   idQuery, isOK := params.Args["id"].(string)
   if isOK {
      pidpath := "/participant/" + idQuery
      fs, err := getFriendsInner(pidpath)
      if err != nil {
         log.Printf("user: %s, cannot read friends response: %s", idQuery, err)
         return nil, err
      }
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
     return rv, errors.New("cannot get participant")
   }
   defer resp.Body.Close()
   body, err := io.ReadAll(resp.Body)
   if err != nil {
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
      rv, err := getParticipantInner("/participant/" + idQuery)
      if err != nil {
         log.Printf("user: %s, cannot get participant: %s", idQuery, err)
         return nil, err
      }
      return rv, nil
   }
   return nil, errors.New("participant id not specified")
}

type InboundInner struct {
   From string `json:"from"`
   Occurred string `json:"occurred,omitempty"`
   Subject string `json:"subject,omitempty"`
   Story string `json:"story,omitempty"`
}

func getInbound(params graphql.ResolveParams) (interface{}, error) {
   idQuery, isOK := params.Args["id"].(string)
   if isOK {
      resp, err := http.Get("http://feed:8080/participant/" + idQuery + "/inbound")
      if err != nil {
         log.Printf("user: %s, cannot get inbound: %s", idQuery, err)
         return nil, errors.New("cannot get inbound")
      }
      defer resp.Body.Close()
      body, err := io.ReadAll(resp.Body)
      if err != nil {
         log.Printf("user: %s, cannot read inbound response: %s", idQuery, err)
         return nil, errors.New("cannot read inbound response")
      }
      var ii []InboundInner
      err = json.Unmarshal([]byte(string(body)), &ii)
      if err != nil {
         return nil, errors.New("get inbound invalid response")
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

type OutboundInner struct {
   From string `json:"from,omitempty"`
   Occurred string `json:"occurred,omitempty"`
   Subject string `json:"subject,omitempty"`
   Story string `json:"story,omitempty"`
}

func getOutboundInner(path string) ([]OutboundInner, error) {
   resp, err := http.Get("http://feed:8080" + path + "/outbound")
   if err != nil {
      return nil, errors.New("cannot get outbound")
   }
   defer resp.Body.Close()
   body, err := io.ReadAll(resp.Body)
   if err != nil {
      return nil, errors.New("cannot read outbound response")
   }
   var rv []OutboundInner
   err = json.Unmarshal([]byte(string(body)), &rv)
   if err != nil {
      return nil, errors.New("get outbound invalid response")
   }
   return rv, nil
}

func getOutbound(params graphql.ResolveParams) (interface{}, error) {
   idQuery, isOK := params.Args["id"].(string)
   if isOK {
      oi, err := getOutboundInner("/participant/" + idQuery)
      if err != nil {
         log.Printf("user: %s, cannot get outbound: %s", idQuery, err)
         return nil, err
      }
      rv := make([]Outbound, len(oi), cap(oi))
      for j, o := range oi {
         ot := Outbound{
            Occurred: o.Occurred,
            Subject: o.Subject,
            Story: o.Story,
         }
         rv[j] = ot
      }
      return rv, nil
   }
   return nil, errors.New("participant id not specified")
}

func getSearchResultsInner(keywords string) ([]string, error) {
   resp, err := http.Get("http://feed:8080/outbound?keywords=" + keywords)
   if err != nil {
      return nil, errors.New("cannot search outbound")
   }
   defer resp.Body.Close()
   body, err := io.ReadAll(resp.Body)
   if err != nil {
      return nil, errors.New("cannot read search response")
   }
   var rv []string
   err = json.Unmarshal([]byte(string(body)), &rv)
   if err != nil {
      return nil, errors.New("search invalid response")
   }
   return rv, nil
}

func getSearchResults(params graphql.ResolveParams) (interface{}, error) {
   idQuery, isOK := params.Args["id"].(string)
   if isOK {
      kwQuery, isOK := params.Args["keywords"].(string)
      if isOK {
         ppl, err := getSearchResultsInner(kwQuery)
         if err != nil {
            log.Printf("user: %s error getting search results for keyword %s: %s", idQuery, kwQuery, err)
            return nil, err
         }
         fs, err := getFriendsInner("/participant/" + idQuery)
         if err != nil {
            log.Printf("user: %s, cannot read friends response: %s", idQuery, err)
            return nil, err
         }
         rv := make([]SearchResult, len(ppl), cap(ppl))
         var i = 0
         for _, p := range ppl {
            part, err := getParticipantInner(p)
            if err != nil {
               log.Printf("user: %s, cannot get participant: %s", idQuery, p)
               return nil, err
            }
            outb, err := getOutboundInner(p)
            if err != nil {
               log.Printf("user: %s, cannot get outbound for participant: %s", idQuery, p)
               return nil, err
            }
            if len(outb) == 0 {
               log.Printf("user: %s, no outbound for matching participant: %s", idQuery, p)
               return nil, errors.New("matching participant has no outbound")
            }
            if !isFriend(part.Link, fs) {
               oo := Outbound{
                 Occurred: outb[0].Occurred,
                 Subject: outb[0].Subject,
                 Story: outb[0].Story,
               }
               rv[i] = SearchResult{
                  Participant: part,
                  Outbound: oo,
               }
               i = i + 1
            }
         }
         return rv, nil
      }
      return nil, errors.New("keywords not specificed")
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

