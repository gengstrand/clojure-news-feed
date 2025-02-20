import { Test, TestingModule } from '@nestjs/testing';
import {
  ParticipantApiController,
  Participant,
  Friend,
  Inbound,
  Outbound,
} from './participant.controller';
import { Friend as FriendEntity } from '../entity/friend';
import { Participant as ParticipantEntity } from '../entity/participant';
import {
  ParticipantService,
  OutboundService,
  InboundService,
  InboundModel,
  OutboundModel,
  ParticipantModel,
} from './participant.service';
import { SearchService } from '../search.service';

describe('ParticipantApiController', () => {
  let appController: ParticipantApiController | null = null;
  let participantSaved: number = 0;
  let friendSaved: number = 0;
  let inboundSaved: number = 0;
  let outboundSaved: number = 0;
  const nowAsDate: Date = new Date();
  const nowAsDateOnly: string = nowAsDate.toISOString().slice(0, 10)

  beforeEach(async () => {
    const pe: ParticipantEntity = new ParticipantEntity();
    pe.participantId = 1;
    pe.moniker = 'Hello World!';
    const fe: FriendEntity = new FriendEntity();
    fe.friendsId = 1;
    fe.fromParticipantId = 1;
    fe.toParticipantId = 2;
    const pm1: ParticipantModel = new ParticipantModel(1, 'Hello World!');
    const pm2: ParticipantModel = new ParticipantModel(2, 'foo bar');
    const im: InboundModel = new InboundModel(
      pm1,
      pm2,
      nowAsDate,
      'test subject',
      'test story',
    );
    const om: OutboundModel = new OutboundModel(
      pm1,
      nowAsDate,
      'test subject',
      'test story',
    );
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
          provide: 'PARTICIPANT_REPOSITORY',
          useValue: {
            findOneBy: jest.fn().mockReturnValue(Promise.resolve(pe)),
            save: jest.fn().mockImplementation(() => {
              participantSaved++;
              return Promise.resolve(pe);
            }),
          },
        },
        {
          provide: 'FRIEND_REPOSITORY',
          useValue: {
            save: jest.fn().mockImplementation(() => {
              friendSaved++;
              return Promise.resolve(fe);
            }),
            createQueryBuilder: jest.fn().mockReturnValue({
              where: jest.fn().mockReturnThis(),
              getMany: jest.fn().mockReturnValue(Promise.resolve([fe])),
            }),
          },
        },
        {
          provide: OutboundService,
          useValue: {
            get: jest.fn().mockReturnValue(Promise.resolve([om])),
            save: jest.fn().mockImplementation(() => {
              outboundSaved++;
              return Promise.resolve(om);
            }),
          },
        },
        {
          provide: InboundService,
          useValue: {
            get: jest.fn().mockReturnValue(Promise.resolve([im])),
            save: jest.fn().mockImplementation(() => {
              inboundSaved++;
              return Promise.resolve(im);
            }),
          },
        },
        {
          provide: SearchService,
          useValue: {
            search: jest.fn(),
            index: jest.fn(),
          },
        },
        ParticipantService,
      ],
    }).compile();

    appController = app.get<ParticipantApiController>(ParticipantApiController);
  });

  describe('participant', () => {
    it('fetch should return mocked entity', async () => {
      const p: Participant = new Participant(1, 'Hello World!', '/participant/1');
      const t: Participant | undefined = await appController?.getParticipant(1);
      expect(t).toStrictEqual(p);
    });
    it('add should call underlying repository add', async () => {
      const p: Participant = new Participant(1, 'Hello World!', '/participant/1');
      const t: Participant | undefined = await appController?.addParticipant(p);
      expect(participantSaved).toBeGreaterThan(0);
      expect(t).toStrictEqual(p);
    });
  });
  describe('friend', () => {
    it('fetch should return mocked entity', async () => {
      const t: Friend[] | undefined = await appController?.getFriends(1);
      expect(t?.length).toBe(1);
      expect(t?.[0].from).toBe('/participant/1');
      expect(t?.[0].to).toBe('/participant/2');
    });
    it('add should call underlying repository add', async () => {
      const p1: Participant = new Participant(1, 'Hello World!', '/participant/1');
      const p2: Participant = new Participant(2, 'foo bar', '/participant/2');
      const f: Friend = new Friend(1, p1.link, p2.link);
      const t: Friend | undefined = await appController?.addFriend(1, f);
      expect(friendSaved).toBeGreaterThan(0);
      expect(t).toStrictEqual(f);
    });
  });
  describe('inbound', () => {
    it('fetch should return mocked entity', async () => {
      const t: Inbound[] | undefined = await appController?.getInbound(1);
      expect(t?.length).toBe(1);
      expect(t?.[0].from).toBe('/participant/1');
      expect(t?.[0].to).toBe('/participant/2');
      expect(t?.[0].occurred).toStrictEqual(nowAsDateOnly);
      expect(t?.[0].subject).toBe('test subject');
      expect(t?.[0].story).toBe('test story');
    });
  });
  describe('outbound', () => {
    it('fetch should return mocked entity', async () => {
      const t: Outbound[] | undefined = await appController?.getOutbound(1);
      expect(t?.length).toBe(1);
      expect(t?.[0].from).toBe('/participant/1');
      expect(t?.[0].occurred).toStrictEqual(nowAsDate);
      expect(t?.[0].subject).toBe('test subject');
      expect(t?.[0].story).toBe('test story');
    });
    it('add should call underlying repository add', async () => {
      const p1: Participant = new Participant(1, 'Hello World!', '/participant/1');
      const o: Outbound = new Outbound(
        p1.link,
        nowAsDate,
        'test subject',
        'test story',
      );
      const t: Outbound | undefined = await appController?.addOutbound(1, o);
      expect(outboundSaved).toBeGreaterThan(0);
      expect(inboundSaved).toBeGreaterThan(0);
      expect(t).toStrictEqual(o);
    });
  });
});
