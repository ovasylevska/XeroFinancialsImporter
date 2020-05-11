ALTER TABLE bank_transactions.bank_accounts ADD unique_hash varchar(1000);
ALTER TABLE bank_transactions.bank_accounts ADD CONSTRAINT bank_accounts_unique_hash UNIQUE (unique_hash);
