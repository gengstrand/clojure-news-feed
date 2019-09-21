import * as sql from './sqldb'

export class ParticipantModel {
   readonly id: number
   readonly name: string
   constructor(id: number, moniker: string) {
      this.id = id
      this.name = moniker
   }
}

export class ParticipantService extends sql.Repository {
   constructor(dbHost: string, dbName: string, user: string, password: string, cacheHost: string) {
      super(dbHost, dbName, user, password, cacheHost)
   }

   public get(id: number): ParticipantModel {
      // TODO: query mysql db
      const n = "test name"
      return new ParticipantModel(id, n)
   }

   public save(p: ParticipantModel): ParticipantModel {
      // TODO: insert into mysql db and return id
      const i = 0
      return new ParticipantModel(i, p.name)
   }
}