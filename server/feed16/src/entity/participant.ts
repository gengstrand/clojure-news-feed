import { Entity, Column, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class Participant {
  @PrimaryGeneratedColumn('increment', { name: 'ParticipantID' })
  participantId: number;

  @Column({ name: 'Moniker' })
  moniker: string;
}