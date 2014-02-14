drop procedure if exists InsertTimeData;
drop procedure if exists InsertFactData;
drop table if exists timeperiod;
drop table if exists entity;
drop table if exists activity;
drop table if exists fact;

create table timeperiod (
       time_id int not null primary key auto_increment,
       year int not null,
       month int not null,
       day int not null,
       hour int not null,
       minute int not null
);
create table entity (
       entity_id int not null primary key auto_increment,
       name varchar(30) not null
);
create table activity (
       activity_id int not null primary key auto_increment,
       name varchar(50) not null
);
create table fact (
       throughput int not null,
       mode int not null,
       vigintile int not null,
       activity_id int not null,
       entity_id int not null,
       time_id int not null
);
insert into entity (name) values ('Friend');
insert into entity (name) values ('Inbound');
insert into entity (name) values ('Outbound');
insert into entity (name) values ('Participant');
insert into activity (name) values ('get');
insert into activity (name) values ('load');
insert into activity (name) values ('post');
insert into activity (name) values ('search');
insert into activity (name) values ('store');

delimiter &&

create procedure InsertTimeData (
       in $year int,
       in $month int,
       in $day int,
       in $hour int,
       in $minute int)
begin
	insert into timeperiod (year, month, day, hour, minute ) values ($year, $month, $day, $hour, $minute);
	select last_insert_id() as id;
end&&

create procedure InsertFactData (
       in $throughput int,
       in $mode int,
       in $vigintile int,
       in $activity_id int,
       in $entity_id int,
       in $time_id int)
begin
	insert into fact (throughput, mode, vigintile, activity_id, entity_id, time_id) values ($throughput, $mode, $vigintile, $activity_id, $entity_id, $time_id);
	select last_insert_id() as id;
end&&




