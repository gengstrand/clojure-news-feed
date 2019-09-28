import * as sql from './sqldb'
import {Participant} from './entity/participant'
import {Connection} from "typeorm";

export class ParticipantModel {
   readonly id: number
   readonly name: string
   constructor(id: number, moniker: string) {
      this.id = id
      this.name = moniker
   }
}

export class ParticipantService extends sql.Repository {
   constructor(connection: Connection, cacheHost: string) {
      super(connection, cacheHost)
   }

   public async get(id: number): Promise<ParticipantModel> {
       let r = this.connection.getRepository(Participant);
       let dbp = await r.findOne(id)
       return new ParticipantModel(id, dbp.Moniker)
   }

   public async save(p: ParticipantModel): Promise<ParticipantModel> {
       let r = this.connection.getRepository(Participant)
       let dbp = new Participant()
       dbp.Moniker = p.name;
       let sp = await r.save(dbp)
       return new ParticipantModel(sp.ParticipantID, p.name)
   }
}