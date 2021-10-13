CREATE TABLE j_user
(
    id       serial PRIMARY KEY,
    name     varchar(2000) UNIQUE,
    email    varchar(2000),
    password varchar(55)
);

--insert into j_user values (1, 'Admin', 'root@local', 'root');
--commit;
