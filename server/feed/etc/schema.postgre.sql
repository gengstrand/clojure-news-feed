drop function if exists FetchFriends(in _fromParticipantID int);
drop function if exists UpsertFriends(in _fromParticipantID int, in _toParticipantID int);
drop function if exists FetchParticipant(in _participantID int);
drop function if exists UpsertParticipant(in _moniker varchar(50));
drop index if exists friends_fromid;
drop index if exists friends_toid;
drop table if exists Friends;
drop table if exists Participant;
drop sequence if exists Friends_seq;
drop sequence if exists Participant_seq;

create sequence Participant_seq;

create table Participant (
id int not null primary key default nextval('Participant_seq'),
pname varchar(50));

create sequence Friends_seq;

create table Friends (
id int not null primary key default nextval('Friends_seq'),
fromid int not null references Participant(id),
toid int not null references Participant(id));

create index friends_fromid on Friends (fromid);

create index friends_toid on Friends (toid);

create function UpsertParticipant (
in _moniker varchar(50)) 
returns table(id bigint) as $$
begin
insert into Participant (pname) values ($1);
return query select lastval();
end$$ language plpgsql;

create function FetchParticipant (
in _participantID int) 
returns table (moniker varchar) as $$
begin
return query select pname from Participant where id = $1;
end$$ language plpgsql;

create function UpsertFriends (
in _fromParticipantID int,
in _toParticipantID int)
returns table (id bigint) as $$
begin
insert into Friends (fromid, toid) values ($1, $2);
return query select lastval();
end$$ language plpgsql;

create function FetchFriends(
in _fromParticipantID int)
returns table (FriendsID integer, ParticipantID integer) as $$
begin
return query 
select id, toid as pid from Friends where fromid = $1
union
select id, fromid as pid from Friends where toid = $1;
end$$ language plpgsql;
