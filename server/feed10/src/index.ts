import { GraphQLServer } from 'graphql-yoga'
import * as p from './participant'
import * as f from './friend'
import * as i from './inbound'
import * as o from './outbound'

const typeDefs = `
type Inbound {
    from: Participant
    to: Participant
    occurred: String
    subject: String
    story: String
}
type Outbound {
    from: Participant
    occurred: String
    subject: String
    story: String
}
type Friend {
    from: Participant
    to: Participant
}
type Participant {
    id: ID!
    name: String
    friends: [Friend]
    inbound: [Inbound]
    outbound: [Outbound]
}
type Query {
    participant(id: ID!): Participant
    posters(keywords: String!): [Participant]
}
input CreateParticipantRequest {
    name: String
}
input CreateFriendRequest {
    from_id: ID!
    to_id: ID!
}
input CreateOutboundRequest {
    from_id: ID!
    occurred: String
    subject: String
    story: String

}
type Mutation {
    createParticipant(input: CreateParticipantRequest): Participant
    createFriend(input: CreateFriendRequest): Friend
    createOutbound(input: CreateOutboundRequest): Outbound
}
`
let dbHost = process.env.MYSQL_HOST
let dbName = process.env.MYSQL_DB
let user = process.env.MYSQL_USER
let pwd = process.env.MYSQL_PASS
let cacheHost = process.env.REDIS_HOST
let nosqlHost = process.env.NOSQL_HOST

const participantService = new p.ParticipantService(dbHost, dbName, user, pwd, cacheHost)
const friendService = new f.FriendService(dbHost, dbName, user, pwd, cacheHost)
const inboundService = new i.InboundService(nosqlHost)
const outboundService = new o.OutboundService(nosqlHost, friendService, inboundService)
const resolvers = {
  Query: {
    participant: (_, { id }) => ({ id }),
    posters: (_, { keywords }) => {
      const returnValue = `posters $keywords`
      return returnValue
    },
  },
  Participant: {
    name: ({ id }) => {
      return participantService.get(id).name
    },
    friends: ({ id }) => {
      return friendService.get(id)
    },
    inbound: ({ id }) => {
      return inboundService.get(id)
    },
    outbound: ({ id }) => {
      return outboundService.get(id)
    },
  },
  Mutation: {
    createParticipant(_, args) {
       const np = new p.ParticipantModel(0, args.input.name)
       return participantService.save(np)
    },
    createFriend(_, args) {
       const fp = new p.ParticipantModel(args.input.from_id, null)
       const tp = new p.ParticipantModel(args.input.to_id, null)
       const nf = new f.FriendModel(0, fp, tp)
       return friendService.save(nf)
    },
    createOutbound(_, args) {
       const np = new p.ParticipantModel(args.input.from_id, null)
       const no = new o.OutboundModel(np, args.input.occurred, args.input.subject, args.input.story)
       return outboundService.save(no)
    }
  }
}

const server = new GraphQLServer({
  typeDefs,
  resolvers
})

server.start(() => console.log('Server is running on http://localhost:4000'))
