import * as nosql from './nosqldb'
import * as p from './participant'
import * as f from './friend'
import * as i from './inbound'

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
   readonly friendService: f.FriendService
   readonly inboundService: i.InboundService
   constructor(nosqlHost: string, fs: f.FriendService, is: i.InboundService) {
      super(nosqlHost)
      this.friendService = fs
      this.inboundService = is
   }
   public get(id: number): OutboundModel[] {
      // TODO: query cassandra
      const np = new p.ParticipantModel(1, "test participant")
      const ob = new OutboundModel(np, new Date("2019-09-21"), "test subject", "test story")
      return [ob]
   }
   public save(o: OutboundModel): OutboundModel {
      // TODO: insert into cassandra outbound
      this.friendService.get(o.from.id).forEach((friend) => {
         const nim = new i.InboundModel(o.from, friend.to, o.occurred, o.subject, o.story)
	 this.inboundService.save(nim)
      })
      // TODO: insert into elasticsearch
      return o
   }
}
