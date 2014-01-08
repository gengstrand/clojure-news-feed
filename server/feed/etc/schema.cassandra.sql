create keyspace activity with replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

use activity;

create table Inbound (
ParticipantID int,
Occurred timeuuid,
FromParticipantID int,
Subject text,
Story text,
primary key (ParticipantID, Occurred));

create table Outbound (
ParticipantID int,
Occurred timeuuid,
Subject text,
Story text,
primary key (ParticipantID, Occurred));

