import { Client } from 'cassandra-driver'

export class Repository {
   protected nosqlClient: Client
   constructor(nosqlClient: Client) {
      this.nosqlClient = nosqlClient
   }
}