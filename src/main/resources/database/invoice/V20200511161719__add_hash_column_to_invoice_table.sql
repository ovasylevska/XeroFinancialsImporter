ALTER TABLE invoices.invoices ADD unique_hash varchar(1000);
ALTER TABLE invoices.invoices ADD CONSTRAINT invoices_unique_hash UNIQUE (unique_hash);
