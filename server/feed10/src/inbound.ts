import * as nosql from './nosqldb'
import * as o from './outbound'
import * as p from './participant'

export class InboundModel extends o.OutboundModel{
   readonly to: p.ParticipantModel
   constructor(from: p.ParticipantModel, to: p.ParticipantModel, occurred: Date, subject: string, story: string) {
      super(from, occurred, subject, story)
      this.to = to
   }
}

export class InboundService extends nosql.Repository {
   constructor(nosqlHost: string) {
      super(nosqlHost)
   }
   public get(id: number): InboundModel[] {
      // TODO: query cassandra
      const fp = new p.ParticipantModel(1, "test participant 1")
      const tp = new p.ParticipantModel(2, "test participant 2")
      const ib = new InboundModel(fp, tp, new Date("2019-09-21"), "test subject", "test story")
      return [ib]
   }
   public save(o: InboundModel): InboundModel {
      // todo: insert into cassandra
      return o
   }
}
