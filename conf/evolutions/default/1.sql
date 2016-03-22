# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table credit_card (
  id                            integer auto_increment not null,
  digits                        varchar(255),
  exp_date                      varchar(255),
  cvv                           varchar(255),
  user_id                       integer,
  address                       varchar(255),
  zip                           varchar(255),
  state                         varchar(255),
  constraint pk_credit_card primary key (id)
);

create table credit_report (
  id                            integer auto_increment not null,
  bureau                        varchar(255),
  constraint pk_credit_report primary key (id)
);

create table credit_report_field (
  id                            integer auto_increment not null,
  name                          varchar(255),
  value                         varchar(255),
  credit_report_id              integer,
  constraint pk_credit_report_field primary key (id)
);

create table payment_gateway (
  id                            integer auto_increment not null,
  name                          varchar(255),
  constraint pk_payment_gateway primary key (id)
);

create table product (
  id                            integer auto_increment not null,
  type                          varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  price                         varchar(255),
  sale_price                    varchar(255),
  constraint pk_product primary key (id)
);

create table transaction (
  id                            integer auto_increment not null,
  user_id                       integer,
  credit_card_id                integer,
  product_id                    integer,
  constraint pk_transaction primary key (id)
);

create table user (
  id                            integer auto_increment not null,
  first_name                    varchar(255),
  last_name                     varchar(255),
  email                         varchar(255),
  phone                         varchar(255),
  address                       varchar(255),
  city                          varchar(255),
  state                         varchar(255),
  zip                           varchar(255),
  password                      varchar(255),
  token                         varchar(255),
  active                        tinyint(1) default 0,
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id)
);

alter table credit_card add constraint fk_credit_card_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_credit_card_user_id on credit_card (user_id);

alter table credit_report_field add constraint fk_credit_report_field_credit_report_id foreign key (credit_report_id) references credit_report (id) on delete restrict on update restrict;
create index ix_credit_report_field_credit_report_id on credit_report_field (credit_report_id);

alter table transaction add constraint fk_transaction_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_transaction_user_id on transaction (user_id);

alter table transaction add constraint fk_transaction_credit_card_id foreign key (credit_card_id) references credit_card (id) on delete restrict on update restrict;
create index ix_transaction_credit_card_id on transaction (credit_card_id);

alter table transaction add constraint fk_transaction_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_transaction_product_id on transaction (product_id);


# --- !Downs

alter table credit_card drop foreign key fk_credit_card_user_id;
drop index ix_credit_card_user_id on credit_card;

alter table credit_report_field drop foreign key fk_credit_report_field_credit_report_id;
drop index ix_credit_report_field_credit_report_id on credit_report_field;

alter table transaction drop foreign key fk_transaction_user_id;
drop index ix_transaction_user_id on transaction;

alter table transaction drop foreign key fk_transaction_credit_card_id;
drop index ix_transaction_credit_card_id on transaction;

alter table transaction drop foreign key fk_transaction_product_id;
drop index ix_transaction_product_id on transaction;

drop table if exists credit_card;

drop table if exists credit_report;

drop table if exists credit_report_field;

drop table if exists payment_gateway;

drop table if exists product;

drop table if exists transaction;

drop table if exists user;

