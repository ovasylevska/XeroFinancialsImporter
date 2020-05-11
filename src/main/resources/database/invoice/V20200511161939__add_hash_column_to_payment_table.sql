ALTER TABLE invoices.payments ADD unique_hash varchar(1000);
ALTER TABLE invoices.payments ADD CONSTRAINT payments_unique_hash UNIQUE (unique_hash);
