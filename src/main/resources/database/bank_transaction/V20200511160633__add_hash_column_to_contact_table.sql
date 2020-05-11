ALTER TABLE bank_transactions.contacts ADD unique_hash varchar(1000);
ALTER TABLE bank_transactions.contacts ADD CONSTRAINT contacts_unique_hash UNIQUE (unique_hash);
