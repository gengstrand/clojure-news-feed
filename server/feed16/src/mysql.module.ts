
import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Friend } from './entity/friend';
import { Participant } from './entity/participant';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'mysql',
      host: process.env["MYSQL_HOST"] ?? 'localhost',
      port: 3306,
      username: 'feed',
      password: 'feed1234',
      database: 'feed',
      entities: [Friend, Participant],
      synchronize: false,
    }),
  ],
})
export class MySqlModule {}
