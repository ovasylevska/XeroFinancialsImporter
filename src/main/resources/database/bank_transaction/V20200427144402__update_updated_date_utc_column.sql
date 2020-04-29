ALTER TABLE bank_transactions.bank_transactions DROP updated_date_utc CASCADE ;
ALTER TABLE bank_transactions.bank_transactions ADD updated_date_utc timestamp;
