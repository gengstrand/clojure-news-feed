import { Test, TestingModule } from '@nestjs/testing';
import { ParticipantApiController, Participant } from './participant.controller';
import { ParticipantService } from './participant.service';

describe('ParticipantApiController', () => {
  let appController: ParticipantApiController;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [ParticipantApiController],
      providers: [ParticipantService],
    }).compile();

    appController = app.get<ParticipantApiController>(ParticipantApiController);
  });

  describe('root', () => {
    it('should return "Hello World!"', () => {
      let p: Participant = new Participant(1, 'Hello World!');
      expect(appController.getParticipant(1)).toBe(p);
    });
  });
});
