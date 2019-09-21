import { GraphQLServer } from 'graphql-yoga'
import * as p from './participant'
import * as f from './friend'
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
const participantService = new p.ParticipantService(dbHost, dbName, user, pwd, cacheHost)
const friendService = new f.FriendService(dbHost, dbName, user, pwd, cacheHost)

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
      const returnValue = `inbound news for participant $id`
      return returnValue
    },
    outbound: ({ id }) => {
      const returnValue = `participant $id outbound news`
      return returnValue
    },
  },
  Mutation: {
    createParticipant(_, args) {
       const np = new p.ParticipantModel(0, args.input.name)
       return participantService.save(np)
    },
    createFriend(_, args) {
       const nf = new f.FriendModel(0, args.input.from_id, args.input.to_id)
       return friendService.save(nf)
    },
    createOutbound(_, args) {
    }
  }
}

const server = new GraphQLServer({
  typeDefs,
  resolvers
})

server.start(() => console.log('Server is running on http://localhost:4000'))
