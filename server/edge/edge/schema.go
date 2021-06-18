package edge

import (
       "time"
       "github.com/graphql-go/graphql"
)

type Participant struct {

        Id int64 `json:"id,omitempty"`

        Name string `json:"name,omitempty"`
        
        Link string `json:"link,omitempty"`

}

type ParticipantState struct {

        Participant Participant `json:"participant"`

        Friends []Participant `json:"friends"`

        Inbound []Inbound `json:"inbound"`

        Outbound []Outbound `json:"outbound"`
}

type Inbound struct {

        From Participant `json:"from"`

        Occurred time.Time `json:"occurred,omitempty"`

        Subject string `json:"subject,omitempty"`

        Story string `json:"story,omitempty"`
}

type Outbound struct {

        Occurred time.Time `json:"occurred,omitempty"`

        Subject string `json:"subject,omitempty"`

        Story string `json:"story,omitempty"`
}

var participantType = graphql.NewObject(graphql.ObjectConfig{
        Name: "Participant",
        Fields: graphql.Fields{
                "id": &graphql.Field{
                        Type: graphql.ID,
                        Description: "",
                },
                "name": &graphql.Field{
                        Type: graphql.String,
                        Description: "",
                },
                "link": &graphql.Field{
                        Type: graphql.String,
                        Description: "",
                },
        },
})

var InboundType = graphql.NewObject(graphql.ObjectConfig{
    Name: "Inbound",
    Fields: graphql.Fields{
            "from": &graphql.Field{
                Type: participantType,
                Description: "",
            },
            "occurred": &graphql.Field{
                Type: graphql.DateTime,
                Description: "",
            },
            "subject": &graphql.Field{
                Type: graphql.String,
                Description: "",
            },
            "story": &graphql.Field{
                Type: graphql.String,
                Description: "",
            },
    },
})

var OutboundType = graphql.NewObject(graphql.ObjectConfig{
    Name: "Outbound",
    Fields: graphql.Fields{
            "occurred": &graphql.Field{
                Type: graphql.DateTime,
                Description: "",
            },
            "subject": &graphql.Field{
                Type: graphql.String,
                Description: "",
            },
            "story": &graphql.Field{
                Type: graphql.String,
                Description: "",
            },
    },
})

var participantStateType = graphql.NewObject(graphql.ObjectConfig{
        Name: "ParticipantState",
        Fields: graphql.Fields{
                "participant": &graphql.Field{
                        Type: participantType,
                        Description: "",
                        Resolve: getParticipant,
                },
                "friends": &graphql.Field{
                        Type: graphql.NewList(participantType),
                        Description: "",
                        Resolve: getFriends,
                },
                "inbound": &graphql.Field{
                        Type: graphql.NewList(InboundType),
                        Description: "",
                        Resolve: getInbound,
                },
                "outbound": &graphql.Field{
                        Type: graphql.NewList(OutboundType),
                        Description: "",
                        Resolve: getOutbound,
                },
        },
})

var rootQuery = graphql.NewObject(graphql.ObjectConfig{
        Name: "RootQuery",
        Fields: graphql.Fields{
                "participant": &graphql.Field{
                        Type: participantStateType,
                        Description: "",
                        Args: graphql.FieldConfigArgument{
                              "id": &graphql.ArgumentConfig{
                                    Type: graphql.String,
                              },
                        },
                },
        },
})

var NewsFeedSchema, _ = graphql.NewSchema(graphql.SchemaConfig{
        Query:    rootQuery,
})

