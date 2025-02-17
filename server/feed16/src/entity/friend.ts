import { Entity, Column, PrimaryGeneratedColumn, Repository } from 'typeorm';

export class FriendRepository extends Repository<Friend> {}

@Entity('Friends')
export class Friend {
  @PrimaryGeneratedColumn('increment', { name: 'FriendsID' })
  friendsId: number;

  @Column({ name: 'FromParticipantID' })
  fromParticipantId: number;

  @Column({ name: 'ToParticipantID' })
  toParticipantId: number;
}