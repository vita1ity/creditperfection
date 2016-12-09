# --- !Ups
create table discount (
  id                            bigint auto_increment not null,
  subscription_id               bigint not null,
  discount_type                 integer,
  discount_amount               double,
  start_date                    date not null,
  end_date                      date not null,
  discount_status               integer,
  constraint ck_discount_discount_type check (discount_type in (0,1,2)),
  constraint ck_discount_discount_status check (discount_status in (0,1)),
  constraint pk_discount primary key (id)
);

alter table discount add constraint fk_discount_subscription_id foreign key (subscription_id) references subscription (id) on delete restrict on update restrict;
create index ix_discount_subscription_id on discount (subscription_id);

# --- !Downs

alter table discount drop foreign key fk_discount_subscription_id;
drop index ix_discount_subscription_id on discount;

drop table if exists discount;

