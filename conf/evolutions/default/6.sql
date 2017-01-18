# --- !Ups
ALTER TABLE auth_net_account ADD priority integer;

# --- !Downs
ALTER TABLE auth_net_account DROP COLUMN priority;