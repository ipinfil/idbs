truncate table accounts, contracts, rooms, applications,room_types, room_priorities, cards, years, points, operations restart identity cascade;

-- name generation
drop table if exists first_names cascade;
create table first_names
(
    first_name varchar
);

insert into first_names (first_name)
values	('Jan'), ('Viliam'), ('Matej'), ('Marian'), ('Alexander'),
          ('Frederik'), ('Daniel'), ('Timotej'), ('Filip'), ('Frantisek'),
          ('Robert'), ('Eduard'), ('Edmund'), ('Juraj'), ('Jakub'),
          ('Michal'), ('Adam'), ('Kevin'), ('Andrej'), ('Ondrej');

drop table if exists last_names cascade;
create table last_names
(
    last_name varchar
);

insert into last_names (last_name)
values	('Horvath'), ('Kováč'), ('Varga'), ('Tóth'), ('Nagy'),
          ('Baláž'), ('Molnár'), ('Szabó'), ('Balog'), ('Lukáč'),
          ('Novák'), ('Kovács'), ('Polák'), ('Gajdoš'), ('Kollár'),
          ('Hudák'), ('Németh'), ('Kováčik'), ('Oláh'), ('Oravec'),
          ('Gábor'), ('Gábor'), ('Pavlík'), ('Lakatoš'), ('Šimko'),
          ('Martin'), ('Farkaš'), ('Bartoš'), ('Lacko'), ('Urban');

create or replace function random_first_name() returns varchar language sql as
$$
select first_name from first_names order by random() limit 1
$$;

create or replace function random_last_name() returns varchar language sql as
$$
select last_name from last_names order by random() limit 1
$$;


-- generate accounts
insert into accounts (first_name, last_name, credit)
select  random_first_name(),
        random_last_name(),
        floor(random() * 500)::numeric + 1
from generate_series(1, 1000) as seq(i);

-- generate cards
insert into cards (blocked, account_id)
select false,
       a.id
from accounts a;

-- generate room types
insert into room_types (monthly_payment, capacity)
VALUES (45.7, 3), (63.9, 2), (94.2, 1);

create or replace function random_type(dummy_id integer) returns table (id integer, capacity integer) language sql as
$$
select id, capacity from room_types order by random() limit 1
$$;

-- generate rooms
insert into rooms (floor, vacancy, building, room_type_id)
select floor(random() * 5)::integer + 1,
       types.capacity,
       floor(random() * 5)::integer + 1,
       types.id
from generate_series(1, 200) as seq(i)
         cross join lateral random_type(i) as types;

-- generate years
insert into years (value)
select seq.i
from generate_series(2005, 2020) as seq(i);

-- generate points

create or replace function random_years(dummy_id integer) returns table (year_id integer) language sql as
$$
select id from years order by random() limit 1;
$$;

insert into points (amount, year_id, account_id)
select floor(random() * 400)::integer + 100,
       years.year_id + seq.i,
       a.id
from (accounts as a
    cross join lateral random_years(a.id) as years)
         cross join generate_series(0, least(4, 16 - years.year_id)) as seq(i);

-- generovanie applications
insert into applications (year_id, status, account_id, contract_id)
select points.year_id,
       CASE WHEN random() < 0.5 THEN 'Denied' ELSE 'Accepted' END,
       points.account_id,
       null
from points
where random() > 0.3; --ziadost nepodal kazdy

update applications
set status = 'Sent'
where year_id = (SELECT max(id) from years);

-- generovanie room order
create or replace function random_rooms(dummy_id integer) returns table (room_id integer) language sql as
$$
select id from rooms tablesample system_rows(100) order by random() limit floor(random() * 4)::integer + 1
$$;

create or replace function enumarate_random_rooms(dummy_id integer) returns table (row_id bigint, room_id integer) language sql as
$$
select  row_number() over () as row_id, room_id from random_rooms(dummy_id);
$$;

insert into room_priorities (ord, application_id, room_id)
select row_id,
       applications.id,
       rooms.room_id
from applications
         cross join lateral enumarate_random_rooms(applications.id) as rooms;

-- generovanie operations
insert into operations (amount, account_id)
select credit,
       id
from accounts;

create or replace function random_year(dummy_id integer) returns table (id integer, value integer) language sql as
$$
select id, value from years limit 1 offset floor(random()*(SELECT count(*) from years))
$$;


create or replace function random_room(dummy_id integer) returns integer language sql as
$$
select id from rooms tablesample system_rows(100)  order by random() limit 1
$$;

--data zmluv
insert into contracts (status, year_id, valid_since, valid_until, account_id, room_id)
SELECT CASE WHEN random() < 0.5 THEN 'Invalid' ELSE 'Prematurely canceled' END AS status,
       applications.year_id,
       make_date(y.value, 9, 1),
       make_date(y.value + 1, 8, 31),
       account_id,
       random_room(applications.id)
FROM applications
         JOIN years y on applications.year_id = y.id;

ALTER TABLE points ADD CHECK ( amount >= 0 );
ALTER TABLE points ADD UNIQUE (account_id, year_id);
ALTER TABLE operations ADD CHECK ( amount != 0 );
ALTER TABLE room_types ADD CHECK ( monthly_payment > 0 );
ALTER TABLE room_types ADD CHECK ( capacity > 0 );
ALTER TABLE rooms ADD CHECK ( vacancy >= 0 );
ALTER TABLE contracts ADD CHECK ( status in ('Valid', 'Invalid', 'Prematurely canceled') );
ALTER TABLE contracts ADD CHECK (valid_since < valid_until);
ALTER TABLE applications ADD CHECK (status in ('Denied', 'Accepted', 'Sent'));
ALTER TABLE room_priorities ADD CHECK (ord > 0);
ALTER TABLE room_priorities ADD unique (ord, application_id);

create index contracts_status_index on contracts (status);
create unique index cards_account_id_index on cards (account_id);
create index contracts_account_index on contracts (account_id);
create index room_priorities_app_id_index on room_priorities (application_id);
create index applications_status_index on applications (status);
create index contracts_year_id_index on contracts (year_id);

drop table first_names, last_names;
drop function random_last_name();
drop function random_first_name();
drop function random_rooms(integer);
drop function random_type(integer);
drop function random_years(integer);
drop function enumarate_random_rooms(dummy_id integer);
drop function random_room(dummy_id integer);
drop function random_year(dummy_id integer);

drop function if exists generate_contracts();
create or replace function generate_contracts() returns void language sql as --used in room assignment
$$
insert into contracts (status, year_id, valid_since, valid_until, account_id, room_id)
select null,
       apps.year_id,
       make_date(y.value, 9, 1),
       make_date(y.value + 1, 8, 31),
       apps.account_id,
       null
from applications apps join years y on apps.year_id = y.id
where apps.status = 'Sent' AND y.id = (SELECT max(id) from years);
$$;

drop function if exists accept_applications();
create or replace function accept_applications() returns void language sql as --used in room assignment
$$
update applications
set status = 'Accepted',
    contract_id = (SELECT id from contracts where contracts.account_id = applications.account_id AND year_id = (SELECT max(id) from years) AND contracts.status IS NULL)
WHERE status = 'Sent' AND year_id = (SELECT max(id) from years);
$$;

DROP FUNCTION IF EXISTS room_assignment(point_threshold integer);
CREATE OR REPLACE FUNCTION room_assignment(point_threshold integer) RETURNS INTEGER AS
$$
DECLARE
    c contracts;
    a applications;
    rp room_priorities;
    r rooms;
    y years;
    numberOfRows integer;
BEGIN
    numberOfRows := 0;
    SELECT * INTO y FROM years WHERE years.id = (SELECT max(id) FROM years);
    FOR c IN SELECT contracts.id, contracts.status, contracts.year_id, contracts.valid_since, contracts.valid_until, contracts.account_id, contracts.room_id
             FROM contracts
             JOIN points ON contracts.account_id = points.account_id  AND contracts.year_id = points.year_id
             WHERE contracts.status IS NULL AND contracts.year_id = y.id AND points.amount >= point_threshold
             ORDER BY points.amount DESC
        LOOP
            SELECT * INTO STRICT a FROM applications WHERE applications.contract_id = c.id;
            for rp in SELECT * FROM room_priorities WHERE room_priorities.application_id = a.id ORDER BY ord ASC
                LOOP
                    SELECT * INTO STRICT r FROM rooms WHERE rooms.id = rp.room_id;
                    IF r.vacancy != 0 THEN
                        -- room is free
                        r.vacancy := r.vacancy - 1;
                        c.room_id := r.id;

                        UPDATE rooms SET vacancy = vacancy - 1 WHERE id = r.id;
                        UPDATE contracts SET room_id = r.id WHERE id = c.id;
                        numberOfRows := numberOfRows +1;
                        EXIT;
                    end if;
                end loop;

            IF c.room_id IS NOT NULL THEN
                CONTINUE;
            end if;
            IF c.room_id IS NULL THEN
                -- no room from chosen room priorities is free
                SELECT * INTO r FROM rooms WHERE vacancy != 0 LIMIT 1;
                IF NOT FOUND THEN
                    RAISE EXCEPTION 'No free room to assign';
                end if;
                IF FOUND THEN
                    UPDATE rooms SET vacancy = vacancy - 1 WHERE id = r.id;
                    UPDATE contracts SET room_id = r.id WHERE id = c.id;
                    numberOfRows := numberOfRows +1;
                end if;

            end if;
        end loop;
    RETURN numberOfRows;
end;
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS monthly_payment();
CREATE OR REPLACE FUNCTION monthly_payment() RETURNS TABLE (sum numeric, num_of_blocked int, num_of_successful int) AS
$$
DECLARE
    c contracts;
    rt room_types;
    r rooms;
    card cards;
    a accounts;
    amount numeric;
    sum numeric;
    num_of_blocked int;
    num_of_successful int;
BEGIN
    sum := 0;
    num_of_blocked := 0;
    FOR c IN SELECT * FROM contracts WHERE contracts.status = 'Valid'
        LOOP
            SELECT * INTO STRICT a FROM accounts WHERE accounts.id = c.account_id;
            SELECT * INTO STRICT r FROM rooms WHERE rooms.id = c.room_id;
            SELECT * INTO STRICT rt FROM room_types WHERE room_types.id = r.room_type_id;
            SELECT * INTO STRICT card FROM cards WHERE cards.account_id = a.id;
            amount := rt.monthly_payment;

            IF amount <= a.credit THEN
                sum := sum + amount;
                INSERT INTO operations (amount, account_id) VALUES (amount, a.id);
                UPDATE accounts SET credit = credit - amount WHERE accounts.id = a.id;
                num_of_successful := num_of_successful + 1;
                continue;
            end if;

            IF amount > a.credit THEN
                UPDATE cards SET blocked = true WHERE cards.id = card.id;
                num_of_blocked := num_of_blocked + 1;
            end if;

        end loop;
    RETURN QUERY SELECT sum, num_of_blocked, num_of_successful;
end;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION new_card() RETURNS trigger AS
$$
BEGIN
    INSERT INTO cards (blocked, account_id) VALUES (false, NEW.id);
    RETURN NEW;
end;
$$
    LANGUAGE plpgsql;

CREATE TRIGGER account_card_insert_trigger
    AFTER INSERT ON accounts
    FOR EACH ROW EXECUTE PROCEDURE new_card();