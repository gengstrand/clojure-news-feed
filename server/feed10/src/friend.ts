import {ParticipantModel} from './participant'
import {Repository} from './sqldb'
import {Friends} from './entity/friend'
import {Connection} from "typeorm";
import {RedisClient} from 'redis'

export class FriendModel {
   readonly id: number
   readonly from: ParticipantModel
   readonly to: ParticipantModel
   constructor(id: number, from: ParticipantModel, to: ParticipantModel) {
      this.id = id
      this.from = from
      this.to = to
   }
}

export class FriendService extends Repository {
   constructor(connection: Connection, cache: RedisClient) {
      super(connection, cache)
   }
   public async get(id: number): Promise<FriendModel[]> {
      const key = 'Friends::'.concat(id.toString())
      const reply = await this.cache.get(key)
      const fp = new ParticipantModel(id, "")
      if (reply == null) {
      	 let r = this.connection.getRepository(Friends);
      	 let dbr = await r.find({ FromParticipantID: id })
      	 const fp = new ParticipantModel(id, "")
      	 const retVal = dbr.map((dbf) => {
            const tp = new ParticipantModel(dbf.ToParticipantID, "")
	    return new FriendModel(dbf.FriendsID, fp, tp)
         })
	 this.cache.set(key, JSON.stringify(retVal))
	 return retVal
      } else {
	 const p = JSON.parse(reply)
      	 return p.map((f) => {
            const tp = new ParticipantModel(f.to.id, "")
	    return new FriendModel(f.id, fp, tp)
	 })
      }
   }
   public async save(f: FriendModel): Promise<FriendModel> {
      let r = this.connection.getRepository(Friends)
      let dbf = new Friends()
      dbf.FromParticipantID = f.from.id
      dbf.ToParticipantID = f.to.id
      let sf = await r.save(dbf)
      const key = 'Friends::'.concat(f.from.id.toString())
      this.cache.del(key)
      return new FriendModel(sf.FriendsID, f.from, f.to)
   }
}