CREATE TABLE IF NOT EXISTS bank_transactions.contacts (
	id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	contact_id varchar(250),
	name varchar
);

create index IF NOT EXISTS contact_id_indx on bank_transactions.contacts (contact_id);

