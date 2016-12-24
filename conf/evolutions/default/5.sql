# --- !Ups
ALTER TABLE subscription ADD renew_failed_date date;

# --- !Downs
ALTER TABLE subscription DROP COLUMN renew_failed_date;