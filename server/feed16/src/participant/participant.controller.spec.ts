import { Test, TestingModule } from '@nestjs/testing';
import { ParticipantApiController, Participant } from './participant.controller';
import { Friend as FriendEntity } from '../entity/friend';
import { Participant as ParticipantEntity } from '../entity/participant';
import { ParticipantService, OutboundService, InboundService } from './participant.service';
import { SearchService } from '../search.service';

describe('ParticipantApiController', () => {
  let appController: ParticipantApiController | null = null;

  beforeEach(async () => {
    const pe: ParticipantEntity = new ParticipantEntity();
    pe.participantId = 1;
    pe.moniker = 'Hello World!';
    const app: TestingModule = await Test.createTestingModule({
      imports: [],
      controllers: [ParticipantApiController],
      providers: [
        {
          provide: 'CACHE_MANAGER',
          useValue: {
            get: jest.fn().mockReturnValue(null),
            set: jest.fn(),
            del: jest.fn(),
          },
        },
        {
          provide: 'ParticipantRepository',
          useValue: {
            findOneBy: jest.fn().mockReturnValue(Promise.resolve(pe)),
            save: jest.fn(),
          },
        },
        {
          provide: 'FriendRepository',
          useValue: {
            save: jest.fn(),
            createQueryBuilder: jest.fn().mockReturnValue({
              where: jest.fn().mockReturnThis(),
              getMany: jest.fn().mockReturnValue([]),
            }),
          },
        },
        {
          provide: OutboundService,
          useValue: {
            get: jest.fn(),
          },
        },
        {
          provide: InboundService,
          useValue: {
            get: jest.fn(),
          },
        },
        {
          provide: SearchService,
          useValue: {
            search: jest.fn(),
          },
        },
        ParticipantService,
      ],
    })
    .compile();

    appController = app.get<ParticipantApiController>(ParticipantApiController);
  });

  describe('root', () => {
    it('should return "Hello World!"', async () => {
      const p: Participant = new Participant(1, 'Hello World!');
      const t: Participant | undefined = await appController?.getParticipant(1);
      expect(t).toStrictEqual(p);
    });
  });
});
