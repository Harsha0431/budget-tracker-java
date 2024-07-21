create database ee_budget_tracker;

use ee_budget_tracker;

create table user(
	email varchar(500),
    password varchar(300) not null,
    name varchar(500) not null,
    constraint pk_user primary key(email)
);

create table income(
	id bigint auto_increment not null,
    user_email varchar(500) not null,
    created_at timestamp not null default current_timestamp,
    amount decimal(15, 2) not null,
    description varchar(1000) not null,
    allocated_month smallint not null,
    allocated_year int not null,
    constraint fk_income_user foreign key(user_email) references user(email) on delete cascade on update cascade,
    constraint pk_income primary key(id)
);