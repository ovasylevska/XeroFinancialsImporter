CREATE TABLE bank_transactions (
	id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	bank_transaction_id varchar(250),
	bank_transaction_type varchar(250),
	contact_id varchar(250),
	line_items varchar,
	is_reconciled int,
	bank_transaction_date date,
	reference varchar(250),
	currency_code varchar(250),
	currency_rate decimal,
	url varchar(1000),
	status varchar(250),
	line_amount_types varchar(250),
	sub_total decimal,
	total_tax decimal,
	total decimal,
	prepayment_id varchar(250),
	overpayment_id varchar(250),
	updated_date_utc bigint,
	has_attachments int,
	status_attribute_string varchar(250)
);

create index bank_transaction_id_indx on bank_transactions (bank_transaction_id);

