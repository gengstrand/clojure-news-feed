package newsfeedserver

import (
	"time"
)

type Inbound struct {

	From int64 `json:"from,omitempty"`

	To int64 `json:"to,omitempty"`

	Occurred time.Time `json:"occurred,omitempty"`

	Subject string `json:"subject,omitempty"`

	Story string `json:"story,omitempty"`
}
