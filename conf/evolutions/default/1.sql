# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                            integer not null,
  username                      varchar(255),
  email                         varchar(255),
  password                      varchar(255),
  active                        boolean,
  constraint uq_user_username unique (username),
  constraint pk_user primary key (id)
);
create sequence user_seq;


# --- !Downs

drop table if exists user;
drop sequence if exists user_seq;

