ALTER TABLE bank_transactions.line_items ADD unique_hash varchar(1000);
ALTER TABLE bank_transactions.line_items ADD CONSTRAINT line_items_unique_hash UNIQUE (unique_hash);
