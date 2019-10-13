import * as nosql from './nosqldb'
import * as o from './outbound'
import * as p from './participant'
import { Client } from 'cassandra-driver'

export class InboundModel extends o.OutboundModel{
   readonly to: p.ParticipantModel
   constructor(from: p.ParticipantModel, to: p.ParticipantModel, occurred: Date, subject: string, story: string) {
      super(from, occurred, subject, story)
      this.to = to
   }
}

export class InboundService extends nosql.Repository {
   constructor(nosqlClient: Client) {
      super(nosqlClient)
   }
   public async get(id: number): Promise<InboundModel[]> {
      const query = 'select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc'

      const results = await this.nosqlClient.execute(query, [id], {prepare: true})
      return results.rows.map((row) => {
         const fp = new p.ParticipantModel(row.fromparticipantid, '')
         const tp = new p.ParticipantModel(id, '')
         return new InboundModel(fp, tp, row.occurred, row.subject, row.story)
      })
   }
   public async save(i: InboundModel): Promise<InboundModel> {
      const cql = 'insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?)';
      this.nosqlClient.execute(cql, [i.to.id, i.from.id, i.subject, i.story], {prepare: true})
      return i
   }
}
