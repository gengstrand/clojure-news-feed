drop procedure if exists UpsertParticipant;
drop procedure if exists FetchParticipant;
drop procedure if exists UpsertFriends;
drop procedure if exists FetchFriends;
drop table if exists Friends;
drop table if exists Participant;

create table Participant (
ParticipantID int not null primary key auto_increment,
Moniker varchar(50));

create table Friends (
FriendsID int not null primary key auto_increment,
FromParticipantID int not null,
ToParticipantID int not null,
foreign key (FromParticipantID) references Participant(ParticipantID),
foreign key (ToParticipantID) references Participant(ParticipantID));

delimiter &&

create procedure UpsertParticipant(
in $Moniker varchar(50))
begin
insert into Participant (Moniker) values ($Moniker);
select last_insert_id() as id;
end&&

create procedure FetchParticipant(
in $ParticipantID int)
begin
select Moniker from Participant where ParticipantID = $ParticipantID;
end&&

create procedure UpsertFriends(
in $FromParticipantID int,
in $ToParticipantID int)
begin
insert into Friends (FromParticipantID, ToParticipantID) values ($FromParticipantID, $ToParticipantID);
select last_insert_id() as id;
end&&

create procedure FetchFriends(
in $ParticipantID int)
begin
select FriendsID, ToParticipantID as ParticipantID from Friends where FromParticipantID = $ParticipantID
union
select FriendsID, FromParticipantID as ParticipantID from Friends where ToParticipantID = $ParticipantID;
end&&

