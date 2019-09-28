import * as p from './participant'
import * as sql from './sqldb'
import {Friends} from './entity/friend'
import {Connection} from "typeorm";

export class FriendModel {
   readonly id: number
   readonly from: p.ParticipantModel
   readonly to: p.ParticipantModel
   constructor(id: number, from: p.ParticipantModel, to: p.ParticipantModel) {
      this.id = id
      this.from = from
      this.to = to
   }
}

export class FriendService extends sql.Repository {
   constructor(connection: Connection, cacheHost) {
      super(connection, cacheHost)
   }
   public async get(id: number): Promise<FriendModel[]> {
      let r = this.connection.getRepository(Friends);
      let retVal = await r.find({ FromParticipantID: id })
      const fp = new p.ParticipantModel(id, "")
      return retVal.map((dbf) => {
          const tp = new p.ParticipantModel(dbf.ToParticipantID, "")
	  return new FriendModel(dbf.FriendsID, fp, tp)
      })
   }
   public async save(f: FriendModel): Promise<FriendModel> {
      let r = this.connection.getRepository(Friends)
      let dbf = new Friends()
      dbf.FromParticipantID = f.from.id
      dbf.ToParticipantID = f.to.id
      let sf = await r.save(dbf)
      return new FriendModel(sf.FriendsID, f.from, f.to)
   }
}