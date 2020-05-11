ALTER TABLE bank_transactions.bank_transactions ADD unique_hash varchar(1000);
ALTER TABLE bank_transactions.bank_transactions ADD CONSTRAINT bank_transactions_unique_hash UNIQUE (unique_hash);
