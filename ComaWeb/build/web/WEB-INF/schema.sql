
drop table transport_stops;
drop table points;
drop table transports;
drop table station_stops;
drop table means;

create table means (
    id          int not null primary key,
    name        varchar(12)
);

create table station_stops (
    id          int not null primary key,
    name        varchar(40),
    lat         double,
    lng         double
);

create table transports (
    id          int not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    name        varchar(40),
    mean        int,
    type        varchar(20),
    json_data   clob,
    foreign key (mean) references means(id)
);

create table transport_stops (
    stop_id         int not null,
    transport_id    int not null,
    station_order   int not null,
    departure       char(8),
    arrival         char(8),
    foreign key (stop_id) references station_stops(id),
    foreign key (transport_id) references transports(id)
);

create table points (
    edge_id         int not null,
    point_order     int not null,
    lat             double,
    lnt             double
);

insert into means (id, name) values (0, 'train');
insert into means (id, name) values (1, 'tram');
insert into means (id, name) values (2, 'subway');
insert into means (id, name) values (3, 'bus');
insert into means (id, name) values (4, 'foot');


insert into transports (name, mean, type, json_data) values ('Tramvai 5', 1, null, null);
insert into transports (name, mean, type, json_data) values ('Tramvai 6', 1, null, null);
insert into transports (name, mean, type, json_data) values ('Tramvai 11', 1, null, null);
insert into transports (name, mean, type, json_data) values ('Tramvai 13', 1, null, null);
insert into transports (name, mean, type, json_data) values ('Autobuz 28', 3, null, null);
insert into transports (name, mean, type, json_data) values ('Autobuz 41', 3, null, null);
insert into transports (name, mean, type, json_data) values ('Autobuz 42', 3, null, null);
insert into transports (name, mean, type, json_data) values ('Autobuz 46', 3, null, null);


select t.name, ss.lat, ss.lng, ts.departure, ts.arrival
from transports t, transport_stops ts, station_stops ss
where ts.stop_id = ss.id and ts.transport_id = t.id;


/*
select t.id, t.name, t.mean, t.type, t.json_data, ts.departure
from transports t, transport_stops ts
where t.id = ts.transport_id and ts.station_order = 0 and mean = 0
    and departure like '13%'
order by random();
*/