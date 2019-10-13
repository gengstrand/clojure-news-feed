import {Repository} from './sqldb'
import {Participant} from './entity/participant'
import {Connection} from 'typeorm'
import {RedisClient} from 'redis'

export class ParticipantModel {
   readonly id: number
   readonly name: string
   constructor(id: number, moniker: string) {
      this.id = id
      this.name = moniker
   }
}

export class ParticipantService extends Repository {
   constructor(connection: Connection, cache: RedisClient) {
      super(connection, cache)
   }

   public async get(id: number): Promise<ParticipantModel> {
       const key = 'Participant::'.concat(id.toString())
       const reply = await this.cache.get(key)
       if (reply == null) {
       	   let r = this.connection.getRepository(Participant);
       	   let dbp = await r.findOne(id)
	   this.cache.set(key, JSON.stringify({ id: id, name: dbp.Moniker }))
       	   return new ParticipantModel(id, dbp.Moniker)
       } else {
	   const p = JSON.parse(reply)
	   return new ParticipantModel(p.id, p.name)
       }
   }

   public async save(p: ParticipantModel): Promise<ParticipantModel> {
       let r = this.connection.getRepository(Participant)
       let dbp = new Participant()
       dbp.Moniker = p.name;
       let sp = await r.save(dbp)
       return new ParticipantModel(sp.ParticipantID, p.name)
   }
}