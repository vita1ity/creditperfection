# --- !Ups
create fulltext index livesearch_index on user (first_name,last_name,email);

# --- !Downs
drop index livesearch_index on user;