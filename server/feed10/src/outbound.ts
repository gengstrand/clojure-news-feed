import * as nosql from './nosqldb'
import * as p from './participant'
import * as f from './friend'
import * as i from './inbound'
import { Client } from 'cassandra-driver'
import { SearchService } from './elastic'

export class OutboundModel {
   readonly from: p.ParticipantModel
   readonly occurred: Date
   readonly subject: string
   readonly story: string
   constructor(from: p.ParticipantModel, occurred: Date, subject: string, story: string) {
      this.from = from
      this.occurred = occurred
      this.subject = subject
      this.story = story
   }
}

export class OutboundService extends nosql.Repository {
   readonly participantService: p.ParticipantService
   readonly friendService: f.FriendService
   readonly inboundService: i.InboundService
   readonly searchService: SearchService
   constructor(nosqlClient: Client, ps: p.ParticipantService, fs: f.FriendService, is: i.InboundService, ss: SearchService) {
      super(nosqlClient)
      this.participantService = ps
      this.friendService = fs
      this.inboundService = is
      this.searchService = ss
   }
   public async get(id: number): Promise<OutboundModel[]> {
      const np = new p.ParticipantModel(id, '')
      const query = 'select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc';
      const results = await this.nosqlClient.execute(query, [id], {prepare: true})
      return results.rows.map((row) => {
      	 return new OutboundModel(np, row.occurred, row.subject, row.story)
      })
   }
   
   public async save(o: OutboundModel): Promise<OutboundModel> {
      const cql = 'insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)';
      this.nosqlClient.execute(cql, [o.from.id, o.subject, o.story], {prepare: true})
      const friends = await this.friendService.get(o.from.id)
      friends.forEach((friend) => {
         const nim = new i.InboundModel(o.from, friend.to, o.occurred, o.subject, o.story)
	 this.inboundService.save(nim)
      })
      this.searchService.index(o.from.id, o.story)
      return o
   }

   public async search(term: string): Promise<p.ParticipantModel[]> {
      const results = await this.searchService.search(term)
      return Promise.all(results.map(async (id) => {
         return await this.participantService.get(id)
      }))
   }
}
