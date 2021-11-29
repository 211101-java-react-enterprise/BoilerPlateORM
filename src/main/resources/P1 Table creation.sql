drop table if exists users, accounts;

CREATE TABLE users (
	id serial constraint users_pk primary key,
    first_name varchar(25) not null check (first_name <> ''),
    last_name varchar(25) not null check (last_name <> ''),
    email varchar(255) unique not null check (email <> ''),
    username varchar(20) unique not null check (username <> ''),
    password varchar(255) not null check (password <> '')
);

create table accounts(
	id serial constraint accounts_pk primary key,
	name varchar(50) not null,
	type varchar(8) not null,
	balance numeric(9,2) default 0.00, -- 9,999,999.99
	owner integer not null,
	constraint owner_fk foreign key (owner) references users(id),
	constraint type_chk check (type in ('Checking','Savings')),
	constraint balance_chk check (balance >= 0.00)
);