drop table categories CASCADE;
create table categories (
                        id serial primary key,
                        name varchar(2000)
);
insert into categories values (1, 'Категория 1');
insert into categories values (2, 'Consulting 2');
insert into categories values (3, 'Категория 3');
insert into categories values (4, 'Категория 4');
insert into categories values (5, 'Категория 5');
commit;