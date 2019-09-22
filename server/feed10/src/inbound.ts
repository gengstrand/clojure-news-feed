import * as nosql from './nosqldb'
import * as o from './outbound'

export class InboundModel extends o.OutboundModel{
   readonly to: number
   constructor(from: number, to: number, occurred: Date, subject: string, story: string) {
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
      const ib = new InboundModel(1, 2, new Date("2019-09-21"), "test subject", "test story")
      return [ib]
   }
   public save(o: InboundModel): InboundModel {
      // todo: insert into cassandra
      return o
   }
}
