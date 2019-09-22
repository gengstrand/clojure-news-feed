import * as nosql from './nosqldb'
import * as p from './participant'

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
   constructor(nosqlHost: string) {
      super(nosqlHost)
   }
   public get(id: number): OutboundModel[] {
      // TODO: query cassandra
      const np = new p.ParticipantModel(1, "test participant")
      const ob = new OutboundModel(np, new Date("2019-09-21"), "test subject", "test story")
      return [ob]
   }
   public save(o: OutboundModel): OutboundModel {
      // todo: insert into cassandra
      return o
   }
}
