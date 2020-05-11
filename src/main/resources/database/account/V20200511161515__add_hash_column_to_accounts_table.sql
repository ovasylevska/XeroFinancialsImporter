ALTER TABLE accounts.accounts ADD unique_hash varchar(1000);
ALTER TABLE accounts.accounts ADD CONSTRAINT accounts_unique_hash UNIQUE (unique_hash);
