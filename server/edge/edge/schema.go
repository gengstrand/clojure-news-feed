package edge

import (
       "github.com/graphql-go/graphql"
)

type Participant struct {

        Id int64 `json:"id,omitempty"`

        Name string `json:"name,omitempty"`
        
        Link string `json:"link,omitempty"`

}

type SearchResult struct {

        Participant Participant `json:"participant"`

        Outbound Outbound `json:"outbound"`
}

type Inbound struct {

        From Participant `json:"from"`

        Occurred string `json:"occurred,omitempty"`

        Subject string `json:"subject,omitempty"`

        Story string `json:"story,omitempty"`
}

type Outbound struct {

        Occurred string `json:"occurred,omitempty"`

        Subject string `json:"subject,omitempty"`

        Story string `json:"story,omitempty"`
}

var participantType = graphql.NewObject(graphql.ObjectConfig{
        Name: "Participant",
        Fields: graphql.Fields{
                "id": &graphql.Field{
                        Type: graphql.ID,
                        Description: "uniquely identifies participant",
                },
                "name": &graphql.Field{
                        Type: graphql.String,
                        Description: "name of participant",
                },
                "link": &graphql.Field{
                        Type: graphql.String,
                        Description: "API URI to fetch participant",
                },
        },
})

var InboundType = graphql.NewObject(graphql.ObjectConfig{
    Name: "Inbound",
    Fields: graphql.Fields{
            "from": &graphql.Field{
                Type: participantType,
                Description: "participant who sent this news item",
            },
            "occurred": &graphql.Field{
                Type: graphql.String,
                Description: "date when item was sent",
            },
            "subject": &graphql.Field{
                Type: graphql.String,
                Description: "subject of news item",
            },
            "story": &graphql.Field{
                Type: graphql.String,
                Description: "news item story",
            },
    },
})

var SearchResultType = graphql.NewObject(graphql.ObjectConfig{
    Name: "SearchResults",
    Fields: graphql.Fields{
            "participant": &graphql.Field{
                Type: participantType,
                Description: "matching participant",
            },
            "outbound": &graphql.Field{
                Type: OutboundType,
                Description: "latest post",
            },
    },
})

var OutboundType = graphql.NewObject(graphql.ObjectConfig{
    Name: "Outbound",
    Fields: graphql.Fields{
            "occurred": &graphql.Field{
                Type: graphql.String,
                Description: "date when item was sent",
            },
            "subject": &graphql.Field{
                Type: graphql.String,
                Description: "subject of news item",
            },
            "story": &graphql.Field{
                Type: graphql.String,
                Description: "news item story",
            },
    },
})

var rootQuery = graphql.NewObject(graphql.ObjectConfig{
        Name: "RootQuery",
        Fields: graphql.Fields{
                "me": &graphql.Field{
                        Type: participantType,
                        Description: "currently logged in participant",
                        Args: graphql.FieldConfigArgument{
                              "id": &graphql.ArgumentConfig{
                                    Type: graphql.String,
                              },
                        },
                        Resolve: getParticipant,
                },
                "friends": &graphql.Field{
                        Type: graphql.NewList(participantType),
                        Description: "friends of currently logged in participant",
                        Args: graphql.FieldConfigArgument{
                              "id": &graphql.ArgumentConfig{
                                    Type: graphql.String,
                              },
                        },
                        Resolve: getFriends,
                },
                "inbound": &graphql.Field{
                        Type: graphql.NewList(InboundType),
                        Description: "posts from friends of current participant",
                        Args: graphql.FieldConfigArgument{
                              "id": &graphql.ArgumentConfig{
                                    Type: graphql.String,
                              },
                        },
                        Resolve: getInbound,
                },
                "outbound": &graphql.Field{
                        Type: graphql.NewList(OutboundType),
                        Description: "current participant posts to friends",
                        Args: graphql.FieldConfigArgument{
                              "id": &graphql.ArgumentConfig{
                                    Type: graphql.String,
                              },
                        },
                        Resolve: getOutbound,
                },
                "search": &graphql.Field{
                        Type: graphql.NewList(SearchResultType),
                        Description: "find participants who post items of interest",
                        Args: graphql.FieldConfigArgument{
                              "id": &graphql.ArgumentConfig{
                                    Type: graphql.String,
                              },
                              "keywords": &graphql.ArgumentConfig{
                                    Type: graphql.String,
                              },
                        },
                        Resolve: getSearchResults,
                },
       },
})

var NewsFeedSchema, _ = graphql.NewSchema(graphql.SchemaConfig{
        Query:    rootQuery,
})

