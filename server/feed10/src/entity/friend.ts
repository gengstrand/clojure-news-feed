import {Entity, PrimaryGeneratedColumn, Column} from "typeorm";

@Entity({name: "Friends"})
export class Friends {

    @PrimaryGeneratedColumn()
    FriendsID: number;

    @Column()
    FromParticipantID: number;

    @Column()
    ToParticipantID: number;

}