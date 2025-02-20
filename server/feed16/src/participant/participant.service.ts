import { Injectable, Inject, NotFoundException } from '@nestjs/common';
import { Participant } from '../entity/participant';
import { Friend } from '../entity/friend';
import { Repository } from 'typeorm';
import { CACHE_MANAGER } from '@nestjs/cache-manager';
import { Cache } from 'cache-manager';
import { SearchService, SearchDocument } from '../search.service';

import { Client, types } from 'cassandra-driver';

export class CassandraRepository {
  protected nosqlClient: Client;
  constructor() {
    const distance = types.distance;
    const cassandraHost: string = process.env['NOSQL_HOST'] ?? 'localhost';
    const ks: string = process.env['NOSQL_KEYSPACE'] ?? 'activity';
    this.nosqlClient = new Client({
      contactPoints: [cassandraHost],
      localDataCenter: 'datacenter1',
      keyspace: ks,
      pooling: {
        coreConnectionsPerHost: {
          [distance.local]: 4,
          [distance.remote]: 2,
        },
      },
    });
    this.nosqlClient.connect();
  }
}

export class OutboundModel {
  constructor(
    public readonly from: ParticipantModel,
    public readonly occurred: Date,
    public readonly subject: string,
    public readonly story: string,
  ) {}
}

@Injectable()
export class OutboundService extends CassandraRepository {
  constructor() {
    super();
  }
  public async get(id: number): Promise<OutboundModel[]> {
    const np = new ParticipantModel(id, '');
    const query =
      'select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc';
    const results = await this.nosqlClient.execute(query, [id], {
      prepare: true,
    });
    return results.rows.map((row) => {
      return new OutboundModel(np, row.occurred, row.subject, row.story);
    });
  }

  public async save(o: OutboundModel): Promise<OutboundModel> {
    const cql =
      'insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)';
    await this.nosqlClient.execute(cql, [o.from.id, o.subject, o.story], {
      prepare: true,
    });
    return o;
  }
}

export class InboundModel extends OutboundModel {
  constructor(
    from: ParticipantModel,
    public readonly to: ParticipantModel,
    occurred: Date,
    subject: string,
    story: string,
  ) {
    super(from, occurred, subject, story);
  }
}

export class InboundService extends CassandraRepository {
  constructor() {
    super();
  }
  public async get(id: number): Promise<InboundModel[]> {
    const query =
      'select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc';

    const results = await this.nosqlClient.execute(query, [id], {
      prepare: true,
    });
    return results.rows.map((row) => {
      const fp = new ParticipantModel(row.fromparticipantid, '');
      const tp = new ParticipantModel(id, '');
      return new InboundModel(fp, tp, row.occurred, row.subject, row.story);
    });
  }
  public async save(i: InboundModel): Promise<InboundModel> {
    const cql =
      'insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?)';
    this.nosqlClient.execute(cql, [i.to.id, i.from.id, i.subject, i.story], {
      prepare: true,
    });
    return i;
  }
}

export class ParticipantModel {
  constructor(
    public readonly id: number,
    public readonly name: string,
  ) {}
  accessor link = `/participant/${this.id}`;
  public toString(): string {
    return JSON.stringify(this);
  }
}

export class FriendModel {
  constructor(
    public readonly id: number,
    public readonly from: ParticipantModel,
    public readonly to: ParticipantModel,
  ) {}
  public toString(): string {
    return JSON.stringify(this);
  }
}

@Injectable()
export class ParticipantService {
  constructor(
    @Inject('PARTICIPANT_REPOSITORY')
    private readonly participantRepository: Repository<Participant>,
    @Inject('FRIEND_REPOSITORY')
    private readonly friendRepository: Repository<Friend>,
    private readonly outboundService: OutboundService,
    private readonly inboundService: InboundService,
    @Inject(CACHE_MANAGER) private cacheManager: Cache,
    private readonly searchService: SearchService,
  ) {}

  async addParticipant(body: ParticipantModel): Promise<ParticipantModel> {
    const participant = new Participant();
    participant.moniker = body.name;
    const savedParticipant = await this.participantRepository.save(participant);
    return new ParticipantModel(
      savedParticipant.participantId,
      savedParticipant.moniker,
    );
  }

  async getInbound(id: number): Promise<InboundModel[]> {
    return await this.inboundService.get(id);
  }

  async getParticipant(id: number): Promise<ParticipantModel> {
    const cacheKey = 'Participant::' + `${id}`;
    const cachedParticipant =
      await this.cacheManager.get<ParticipantModel>(cacheKey);
    if (cachedParticipant) {
      return cachedParticipant;
    }
    const participant = await this.participantRepository.findOneBy({
      participantId: id,
    });
    if (!participant) {
      throw new NotFoundException(`Participant ${id} not found`);
    }
    const rv = new ParticipantModel(
      participant.participantId,
      participant.moniker,
    );
    this.cacheManager.set(cacheKey, rv);
    return rv;
  }

  async addFriend(id: number, body: FriendModel): Promise<FriendModel> {
    const friend = new Friend();
    friend.fromParticipantId = id;
    friend.toParticipantId = body.to.id;
    const savedFriend = await this.friendRepository.save(friend);
    this.cacheManager.del('Friend::' + `${id}`);
    this.cacheManager.del('Friend::' + `${body.to.id}`);
    return new FriendModel(savedFriend.friendsId, body.from, body.to);
  }

  async getFriends(id: number): Promise<FriendModel[]> {
    const cacheKey = 'Friend::' + `${id}`;
    const cachedFriends = await this.cacheManager.get<FriendModel[]>(cacheKey);
    if (cachedFriends) {
      return cachedFriends;
    }
    const friends = await this.friendRepository
      .createQueryBuilder('friend')
      .where('friend.fromParticipantId = :id OR friend.toParticipantId = :id', {
        id,
      })
      .getMany();
    const rv = friends.map(
      (f) =>
        new FriendModel(
          f.friendsId,
          new ParticipantModel(f.fromParticipantId, ''),
          new ParticipantModel(f.toParticipantId, ''),
        ),
    );
    this.cacheManager.set(cacheKey, rv);
    return rv;
  }

  async addOutbound(id: number, body: OutboundModel): Promise<OutboundModel> {
    const savedOutbound = await this.outboundService.save(body);
    const friends = await this.getFriends(id);
    for (const friend of friends) {
      const im = new InboundModel(
        body.from,
        friend.to,
        body.occurred,
        body.subject,
        body.story,
      );
      await this.inboundService.save(im);
    }
    const doc = new SearchDocument(id.toString(), body.from.link, body.story);
    await this.searchService.index(doc);
    return savedOutbound;
  }

  async getOutbound(id: number): Promise<OutboundModel[]> {
    return await this.outboundService.get(id);
  }
}
