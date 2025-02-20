import { Controller, Get, Post, HttpCode, HttpStatus, Body, Param } from '@nestjs/common';
import {
  ParticipantService,
  FriendModel,
  ParticipantModel,
  OutboundModel,
} from './participant.service';
import { ApiTags, ApiOperation, ApiResponse } from '@nestjs/swagger';

export class Friend {
  constructor(
    public readonly id: number = 0,
    public readonly from: string,
    public readonly to: string,
  ) {}
}

export class Participant {
  constructor(
    public readonly id: number = 0,
    public readonly name: string,
    public readonly link: string,
  ) {}
}
export class Inbound {
  constructor(
    public readonly from: string,
    public readonly to: string,
    public readonly occurred: string,
    public readonly subject: string,
    public readonly story: string,
  ) {}
}

export class Outbound {
  constructor(
    public readonly from: string,
    public readonly occurred: Date,
    public readonly subject: string,
    public readonly story: string,
  ) {}
}

@ApiTags('participant')
@Controller('participant')
export class ParticipantApiController {
  constructor(private readonly participantApiService: ParticipantService) {}

  @Post(':id/friends')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Create a new friendship' })
  @ApiResponse({
    status: 200,
    description: 'Successful operation',
    type: Friend,
  })
  public async addFriend(
    @Param('id') id: number,
    @Body() body: Friend,
  ): Promise<Friend> {
    const fpm = new ParticipantModel(parseInt(body.from.split('/').pop() as string), '');
    const tpm = new ParticipantModel(parseInt(body.to.split('/').pop() as string), '');
    const fm = new FriendModel(
      body.id,
      fpm,
      tpm,
    );
    const saved = await this.participantApiService.addFriend(id, fm);
    const from = new Participant(saved.from.id, '', fpm.link);
    const to = new Participant(saved.to.id, '', tpm.link);
    return new Friend(saved.id, from.link, to.link);
  }

  @Post(':id/outbound')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Create a participant news item' })
  @ApiResponse({
    status: 200,
    description: 'Successful operation',
    type: Outbound,
  })
  public async addOutbound(
    @Param('id') id: number,
    @Body() body: Outbound,
  ): Promise<Outbound> {
    const om = new OutboundModel(
      new ParticipantModel(id, ''),
      body.occurred,
      body.subject,
      body.story,
    );
    await this.participantApiService.addOutbound(id, om);
    return body;
  }

  @Post()
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Create a new participant' })
  @ApiResponse({
    status: 200,
    description: 'Successful operation',
    type: Participant,
  })
  public async addParticipant(@Body() body: Participant): Promise<Participant> {
    const pm = new ParticipantModel(body.id, body.name);
    const saved = await this.participantApiService.addParticipant(pm);
    return new Participant(saved.id, saved.name, saved.link);
  }

  @Get(':id/friends')
  @ApiOperation({
    summary: 'Retrieve the list of friends for an individual participant',
  })
  @ApiResponse({
    status: 200,
    description: 'Successful operation',
    type: [Friend],
  })
  public async getFriends(@Param('id') id: number): Promise<Friend[]> {
    const rv = await this.participantApiService.getFriends(id);
    return rv.map((f) => {
      const from = new Participant(f.from.id, '', f.from.link);
      const to = new Participant(f.to.id, '', f.to.link);
      return new Friend(f.id, from.link, to.link);
    });
  }

  @Get(':id/inbound')
  @ApiOperation({
    summary: 'Retrieve the inbound feed for an individual participant',
  })
  @ApiResponse({
    status: 200,
    description: 'Successful operation',
    type: [Inbound],
  })
  public async getInbound(@Param('id') id: number): Promise<Inbound[]> {
    const ib = await this.participantApiService.getInbound(id);
    return ib.map((i) => {
      const from = new Participant(i.from.id, '', '/participant/' + `${i.from.id}`);
      const to = new Participant(i.to.id, '', '/participant/' + `${i.to.id}`);
      return new Inbound(from.link, to.link, i.occurred.toISOString().slice(0, 10), i.subject, i.story);
    });
  }

  @Get(':id/outbound')
  @ApiOperation({
    summary: 'Retrieve the news posted by an individual participant',
  })
  @ApiResponse({
    status: 200,
    description: 'Successful operation',
    type: [Outbound],
  })
  public async getOutbound(@Param('id') id: number): Promise<Outbound[]> {
    const rv = await this.participantApiService.getOutbound(id);
    return rv.map((o) => {
      return new Outbound(o.from.link, o.occurred, o.subject, o.story);
    });
  }

  @Get(':id')
  @ApiOperation({ summary: 'Retrieve an individual participant' })
  @ApiResponse({
    status: 200,
    description: 'Successful operation',
    type: Participant,
  })
  public async getParticipant(@Param('id') id: number): Promise<Participant> {
    const pm = await this.participantApiService.getParticipant(id);
    return new Participant(pm.id, pm.name, pm.link);
  }
}
