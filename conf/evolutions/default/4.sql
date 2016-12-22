# --- !Ups
ALTER TABLE product ADD trial_period integer;

# --- !Downs
ALTER TABLE product DROP COLUMN trial_period;