import * as p from './participant'
import * as sql from './sqldb'

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
   constructor(dbHost: string, dbName: string, user: string, password: string, cacheHost) {
      super(dbHost, dbName, user, password, cacheHost)
   }
   public get(id: number): FriendModel[] {
      // TODO: query mysql db
      const fp = new p.ParticipantModel(id, 'from participant')
      const tp = new p.ParticipantModel(2, 'to participant')
      return [new FriendModel(1, fp, tp)]
   }
   
   public save(f: FriendModel): FriendModel {
      // TODO: insert into mysql db and return id
      const i = 0
      return new FriendModel(i, f.from, f.to)
   }
}