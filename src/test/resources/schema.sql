drop table if exists customer CASCADE;

create table customer
(
    id            bigint generated by default as identity,
    customer_name varchar(30) not null,
    primary key (id),
    unique (customer_name)
);

create table vouchers
(
    id            varchar(50),
    type          varchar(30) not null,
    amount        bigint,
    ratio         bigint,
    customer_name varchar(30) not null,
    primary key (id),
    foreign key (customer_name) references customer (customer_name)
);