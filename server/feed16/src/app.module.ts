import { Module } from '@nestjs/common';
import { ParticipantApiController } from './participant/participant.controller';
import { ParticipantService, OutboundService, InboundService } from './participant/participant.service';
import { RedisModule } from './redis.module';
import { SearchModule } from './elasticsearch.module';
import { MySqlModule } from './mysql.module';

@Module({
  imports: [RedisModule, SearchModule, MySqlModule],
  controllers: [ParticipantApiController],
  providers: [ParticipantService, OutboundService, InboundService],
})
export class AppModule {}
