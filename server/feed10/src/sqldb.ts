export class Repository {
   private dbHost: string
   private dbName: string
   private user: string
   private password: string
   private cacheHost: string
   constructor(dbHost: string, dbName: string, user: string, password: string, cacheHost: string) {
      this.dbHost = dbHost
      this.dbName = dbName
      this.user = user
      this.password = password
      this.cacheHost = cacheHost
   }

}