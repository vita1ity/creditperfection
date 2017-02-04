# --- !Ups
ALTER TABLE auth_net_account ADD is_enabled tinyint(1) default 0;

# --- !Downs
ALTER TABLE auth_net_account DROP COLUMN is_enabled;
