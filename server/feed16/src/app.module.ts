import { Module } from '@nestjs/common';
import { ParticipantApiController } from './participant/participant.controller';
import { OutboundApiController } from './outbound/outbound.controller';
import {
  ParticipantService,
  OutboundService,
  InboundService,
} from './participant/participant.service';
import { OutboundService as OutboundSearchService } from './outbound/outbound.service';
import { RedisModule } from './redis.module';
import { SearchModule } from './elasticsearch.module';
import { MySqlModule } from './mysql.module';
import { ParticipantRepository } from './entity/participant';
import { FriendRepository } from './entity/friend';
import { participantProviders, friendProviders } from './database.providers';

@Module({
  imports: [RedisModule, SearchModule, MySqlModule],
  controllers: [ParticipantApiController, OutboundApiController],
  providers: [
    ...participantProviders,
    ...friendProviders,
    ParticipantRepository,
    FriendRepository,
    OutboundService,
    InboundService,
    OutboundSearchService,
    ParticipantService,
  ],
})
export class AppModule {}
