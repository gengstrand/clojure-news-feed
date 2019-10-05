import {Connection} from "typeorm";
import {RedisClient} from 'redis'

export class Repository {
   protected connection: Connection
   protected cache: RedisClient
   constructor(connection: Connection, cache: RedisClient) {
      this.connection = connection
      this.cache = cache
   }

}