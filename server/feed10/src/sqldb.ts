import {Connection} from "typeorm";

export class Repository {
   protected connection: Connection
   private cacheHost: string
   constructor(connection: Connection, cacheHost: string) {
      this.connection = connection
      this.cacheHost = cacheHost
   }

}