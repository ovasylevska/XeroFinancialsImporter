CREATE OR REPLACE VIEW bank_transactions.bank_transactions_total
AS
SELECT
    bt.bank_transaction_id,
    bt.bank_transaction_type,
    bt.is_reconciled,
    bt.bank_transaction_date,
    bt.reference,
    bt.currency_code,
    bt.currency_rate,
    bt.url,
    bt.status,
    bt.line_amount_types,
    bt.sub_total,
    bt.total_tax,
    bt.total,
    bt.prepayment_id,
    bt.overpayment_id,
    bt.updated_date_utc,
    bt.has_attachments,
    bt.status_attribute_string,

    ba.bank_account_id,
    ba.name AS bank_account_name,
    ba.code AS bank_account_code,

    c.contact_id,
    c.name AS contact_name,

    li.line_item_id,
    li.description,
    li.quantity,
    li.unit_amount,
    li.item_code,
    li.account_code,
    li.tax_type,
    li.tax_amount,
    li.line_amount,
    li.tracking,
    li.discount_rate,
    li.discount_amount,
    li.repeating_invoice_id

FROM bank_transactions.bank_transactions bt
       INNER JOIN bank_transactions.bank_accounts ba ON ba.bank_account_id = bt.bank_account_id
       LEFT JOIN bank_transactions.contacts c ON c.contact_id = bt.contact_id
       INNER JOIN bank_transactions.line_items li ON li.bank_transaction_id = bt.bank_transaction_id
