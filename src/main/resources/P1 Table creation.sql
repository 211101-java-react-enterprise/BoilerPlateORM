drop table if exists users;

CREATE TABLE users (
	id serial constraint users_pk primary key,
    first_name varchar(25) not null check (first_name <> ''),
    last_name varchar(25) not null check (last_name <> ''),
    email varchar(255) unique not null check (email <> ''),
    username varchar(20) unique not null check (username <> ''),
    password varchar(255) not null check (password <> '')
);