create database ee_budget_tracker;

use ee_budget_tracker;

create table user(
	email varchar(500),
    password varchar(300) not null,
    name varchar(500) not null,
    constraint pk_user primary key(email)
);