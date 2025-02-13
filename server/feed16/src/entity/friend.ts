import { Entity, Column, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class Friend {
  @PrimaryGeneratedColumn('increment', { name: 'FriendsID' })
  friendsId: number;

  @Column({ name: 'FromParticipantID' })
  fromParticipantId: number;

  @Column({ name: 'ToParticipantID' })
  toParticipantId: number;
}