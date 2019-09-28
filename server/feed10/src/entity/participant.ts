import {Entity, PrimaryGeneratedColumn, Column} from "typeorm";

@Entity({name: "Participant"})
export class Participant {

    @PrimaryGeneratedColumn()
    ParticipantID: number;

    @Column()
    Moniker: string;

}