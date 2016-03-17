# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                            integer not null,
  first_name                    varchar(255),
  last_name                     varchar(255),
  email                         varchar(255),
  zip                           varchar(255),
  password                      varchar(255),
  token                         varchar(255),
  active                        boolean,
  constraint uq_user_first_name unique (first_name),
  constraint pk_user primary key (id)
);
create sequence user_seq;


# --- !Downs

drop table if exists user;
drop sequence if exists user_seq;

