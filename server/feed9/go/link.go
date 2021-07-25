package newsfeedserver

import (
    "log"
    "fmt"
    "time"
    "regexp"
    "strconv"
    "strings"
)

var linkMatcher = regexp.MustCompile(`/participant/([0-9]+)`)

func FormatTimeToString(t time.Time) string {
     return fmt.Sprintf("%d-%02d-%02d", t.Year(), t.Month(), t.Day())
}

func ObtainId(link string) string {
     m := linkMatcher.FindStringSubmatch(link)
     if &m != nil && len(m) > 1 {
     	return m[1]
     }
     return ""
}

func ExtractId(link string) (int64, error) {
     id := ObtainId(link)
     if strings.Compare("", id) == 0 {
     	i, err := strconv.ParseInt(link, 0, 64)
     	if err != nil {
	   log.Printf("cannot convert %s to integer: %s", link, err)
	   return 0, err
        }
     	return i, nil
     }
     i, err := strconv.ParseInt(id, 0, 64)
     if err != nil {
	 log.Printf("cannot convert %s to integer: %s", id, err)
	 return 0, err
     }
     return i, nil
}

func Linkify(id string) string {
     return "/participant/" + id
}

func ToLink(id int64) string {
     return fmt.Sprintf("/participant/%d", id)
}


