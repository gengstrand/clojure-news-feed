import * as os from 'os'
import * as cluster from 'cluster'
import { GraphQLServer } from 'graphql-yoga'
import * as p from './participant'
import * as f from './friend'
import * as i from './inbound'
import * as o from './outbound'
import { SearchService } from './elastic'
import {createConnection, Connection} from 'typeorm'
import {RedisClient} from 'redis'
import {createClient} from 'then-redis'
import { Client, types } from 'cassandra-driver'

if (cluster.isMaster) {
  console.log(`Master ${process.pid} started`)
  const numberOfCores = os.cpus().length
  for (let i = 0; i < numberOfCores; i++) {
    cluster.fork();
  }
} else {
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
  const cacheHost = process.env.REDIS_HOST
  const nosqlHost = process.env.NOSQL_HOST
  const searchHost = process.env.SEARCH_HOST
  const searchPath = process.env.SEARCH_PATH
  const distance = types.distance

  createConnection().then(async connection => {
    const redis = createClient({ host: cacheHost })
    const cassandra = new Client({
      contactPoints: [ nosqlHost ],
      localDataCenter: 'datacenter1',
      keyspace: 'activity',
      pooling: {
         coreConnectionsPerHost: {
            [distance.local]: 4,
            [distance.remote]: 2
         } 
      }
    })
    await cassandra.connect()
    const participantService = new p.ParticipantService(connection, redis)
    const friendService = new f.FriendService(connection, redis)
    const inboundService = new i.InboundService(cassandra)
    const searchService = new SearchService(searchHost, searchPath)
    const outboundService = new o.OutboundService(cassandra, participantService, friendService, inboundService, searchService)
    const resolvers = {
      Query: {
        participant: (_, { id }) => ({ id }),
        posters: (_, { keywords }) => {
          return outboundService.search(keywords)
        },
      },
      Participant: {
        name: async ({ id }) => {
          let retVal = await participantService.get(id)
          return retVal.name
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

    server.start(() => console.log('Server is running on http://localhost:8080'))
  }).catch(error => {
    console.log(error)
  })
}
