package newsfeedserver

import (
	"time"
)

type Inbound struct {

	From string `json:"from,omitempty"`

	To string `json:"to,omitempty"`

	Occurred time.Time `json:"occurred,omitempty"`

	Subject string `json:"subject,omitempty"`

	Story string `json:"story,omitempty"`
}
