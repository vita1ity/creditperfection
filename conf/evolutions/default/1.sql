# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table auth_net_account (
  id                            bigint auto_increment not null,
  name                          varchar(255) not null,
  description                   varchar(255),
  login_id                      varchar(255) not null,
  transaction_key               varchar(255) not null,
  is_last_used                  tinyint(1) default 0,
  constraint pk_auth_net_account primary key (id)
);

create table credit_card (
  id                            bigint auto_increment not null,
  name                          varchar(255) not null,
  card_type                     integer not null,
  digits                        varchar(255) not null,
  exp_date                      date not null,
  cvv                           integer not null,
  user_id                       bigint not null,
  constraint ck_credit_card_card_type check (card_type in (0,1)),
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

create table kbaquestions (
  id                            bigint auto_increment not null,
  url                           varchar(255) not null,
  user_id                       bigint not null,
  constraint uq_kbaquestions_user_id unique (user_id),
  constraint pk_kbaquestions primary key (id)
);

create table payment_gateway (
  id                            integer auto_increment not null,
  name                          varchar(255),
  constraint pk_payment_gateway primary key (id)
);

create table product (
  id                            bigint auto_increment not null,
  name                          varchar(255) not null,
  price                         double not null,
  sale_price                    double not null,
  constraint pk_product primary key (id)
);

create table security_role (
  id                            integer auto_increment not null,
  name                          varchar(255),
  constraint pk_security_role primary key (id)
);

create table subscription (
  id                            bigint auto_increment not null,
  user_id                       bigint not null,
  credit_card_id                bigint not null,
  product_id                    bigint not null,
  status                        integer not null,
  subscription_date             datetime(6) not null,
  last_charge_date              datetime(6) not null,
  constraint ck_subscription_status check (status in (0,1,2,3)),
  constraint uq_subscription_user_id unique (user_id),
  constraint pk_subscription primary key (id)
);

create table transaction (
  id                            bigint auto_increment not null,
  user_id                       bigint not null,
  credit_card_id                bigint not null,
  product_id                    bigint not null,
  amount                        double not null,
  transaction_id                varchar(255) not null,
  status                        integer not null,
  constraint ck_transaction_status check (status in (0,1)),
  constraint pk_transaction primary key (id)
);

create table user (
  id                            bigint auto_increment not null,
  first_name                    varchar(255) not null,
  last_name                     varchar(255) not null,
  email                         varchar(255) not null,
  address                       varchar(255) not null,
  city                          varchar(255) not null,
  state                         varchar(255) not null,
  zip                           varchar(255) not null,
  password                      varchar(255) not null,
  token                         varchar(255) not null,
  active                        tinyint(1) default 0,
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id)
);

create table user_role (
  user_id                       bigint not null,
  security_role_id              integer not null,
  constraint pk_user_role primary key (user_id,security_role_id)
);

alter table credit_card add constraint fk_credit_card_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_credit_card_user_id on credit_card (user_id);

alter table credit_report_field add constraint fk_credit_report_field_credit_report_id foreign key (credit_report_id) references credit_report (id) on delete restrict on update restrict;
create index ix_credit_report_field_credit_report_id on credit_report_field (credit_report_id);

alter table kbaquestions add constraint fk_kbaquestions_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table subscription add constraint fk_subscription_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table subscription add constraint fk_subscription_credit_card_id foreign key (credit_card_id) references credit_card (id) on delete restrict on update restrict;
create index ix_subscription_credit_card_id on subscription (credit_card_id);

alter table subscription add constraint fk_subscription_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_subscription_product_id on subscription (product_id);

alter table transaction add constraint fk_transaction_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_transaction_user_id on transaction (user_id);

alter table transaction add constraint fk_transaction_credit_card_id foreign key (credit_card_id) references credit_card (id) on delete restrict on update restrict;
create index ix_transaction_credit_card_id on transaction (credit_card_id);

alter table transaction add constraint fk_transaction_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_transaction_product_id on transaction (product_id);

alter table user_role add constraint fk_user_role_user foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_role_user on user_role (user_id);

alter table user_role add constraint fk_user_role_security_role foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;
create index ix_user_role_security_role on user_role (security_role_id);


# --- !Downs

alter table credit_card drop foreign key fk_credit_card_user_id;
drop index ix_credit_card_user_id on credit_card;

alter table credit_report_field drop foreign key fk_credit_report_field_credit_report_id;
drop index ix_credit_report_field_credit_report_id on credit_report_field;

alter table kbaquestions drop foreign key fk_kbaquestions_user_id;

alter table subscription drop foreign key fk_subscription_user_id;

alter table subscription drop foreign key fk_subscription_credit_card_id;
drop index ix_subscription_credit_card_id on subscription;

alter table subscription drop foreign key fk_subscription_product_id;
drop index ix_subscription_product_id on subscription;

alter table transaction drop foreign key fk_transaction_user_id;
drop index ix_transaction_user_id on transaction;

alter table transaction drop foreign key fk_transaction_credit_card_id;
drop index ix_transaction_credit_card_id on transaction;

alter table transaction drop foreign key fk_transaction_product_id;
drop index ix_transaction_product_id on transaction;

alter table user_role drop foreign key fk_user_role_user;
drop index ix_user_role_user on user_role;

alter table user_role drop foreign key fk_user_role_security_role;
drop index ix_user_role_security_role on user_role;

drop table if exists auth_net_account;

drop table if exists credit_card;

drop table if exists credit_report;

drop table if exists credit_report_field;

drop table if exists kbaquestions;

drop table if exists payment_gateway;

drop table if exists product;

drop table if exists security_role;

drop table if exists subscription;

drop table if exists transaction;

drop table if exists user;

drop table if exists user_role;

