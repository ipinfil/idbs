/* Autor: Filip Lajcin */

DROP TABLE if exists accounts cascade;
CREATE TABLE accounts
(
    id serial primary key,
    first_name varchar,
    last_name varchar,
    credit numeric
);

drop table if exists years cascade;
create table years
(
    id serial primary key,
    value integer
);

drop table if exists points cascade;
create table points
(
    id serial primary key,
    amount float,
    year_id integer references years (id),
    account_id integer references accounts (id) on delete cascade
);

drop table if exists cards cascade;
create table cards
(
    id serial primary key,
    blocked boolean,
    account_id integer references accounts (id) on delete cascade
);

drop table if exists operations cascade;
create table operations
(
    id serial primary key,
    amount numeric,
    account_id integer references accounts (id) on delete cascade
);

drop table if exists room_types cascade;
create table room_types
(
    id serial primary key,
    monthly_payment numeric,
    capacity integer
);

drop table if exists rooms cascade;
create table rooms
(
    id serial primary key,
    floor integer,
    vacancy integer,
    building integer,
    room_type_id integer references room_types (id)
);

drop table if exists contracts cascade;
create table contracts
(
    id serial primary key,
    status varchar, --platna, neplatna, predcasne zrusena kvoli preubytovaniu
    year_id integer references years (id),
    valid_since date,
    valid_until date,
    account_id integer references accounts (id) on delete cascade,
    room_id integer references rooms (id)
);

drop table if exists applications cascade;
create table applications
(
    id serial primary key,
    year_id integer references years (id),
    status varchar,
    account_id integer references accounts (id) on delete cascade,
    contract_id integer references contracts (id)
);

drop table if exists room_priorities cascade;
create table room_priorities
(
    id serial primary key,
    ord integer,
    application_id integer references applications (id)on delete cascade,
    room_id integer references rooms (id)
);