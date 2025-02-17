import { Entity, Column, PrimaryGeneratedColumn, Repository } from 'typeorm';

export class ParticipantRepository extends Repository<Participant> {
}

@Entity('Participant')
export class Participant {
  @PrimaryGeneratedColumn('increment', { name: 'ParticipantID' })
  participantId: number;

  @Column({ name: 'Moniker' })
  moniker: string;
}