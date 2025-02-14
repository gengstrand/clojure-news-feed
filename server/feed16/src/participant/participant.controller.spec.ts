import { Test, TestingModule } from '@nestjs/testing';
import { ParticipantApiController, Participant } from './participant.controller';
import { ParticipantService } from './participant.service';
import { RedisModule } from '../redis.module';
import { SearchModule } from '../elasticsearch.module';
import { MySqlModule } from '../mysql.module';

describe('ParticipantApiController', () => {
  let appController: ParticipantApiController | null = null;

  jest.mock('@nestjs/typeorm', () => ({
    TypeOrmModule: {
      forRoot: jest.fn().mockReturnValue({}),
      forFeature: jest.fn().mockReturnValue({}),
    },
    MysqlDriver: {
      createPool: jest.fn().mockReturnValue({}),
    },
  }));
  jest.mock('@nestjs/cache-manager', () => ({
    CacheModule: {
      registerAsync: jest.fn().mockReturnValue({}),
    },
  }));
  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      imports: [RedisModule, SearchModule, MySqlModule],
      controllers: [ParticipantApiController],
      providers: [ParticipantService],
    })
    .useMocker((token) => {
      if (token === ParticipantService) {
        return {
          getParticipant: jest.fn((id: number) => new Participant(id, 'Hello World!')),
          addFriend: jest.fn((id: number, fm: any) => fm),
          getFriends: jest.fn((id: number) => []),
          getOutbound: jest.fn((id: number) => []),
          getInbound: jest.fn((id: number) => []),
        };
      }
    })
    .compile();

    appController = app.get<ParticipantApiController>(ParticipantApiController);
  });

  describe('root', () => {
    it('should return "Hello World!"', () => {
      let p: Participant = new Participant(1, 'Hello World!');
      expect(appController?.getParticipant(1)).toBe(p);
    });
  });
});
