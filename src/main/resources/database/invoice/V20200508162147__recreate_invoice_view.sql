CREATE OR REPLACE VIEW invoices.invoices_total
AS
SELECT DISTINCT
    i.invoice_id,
	  i.invoice_type,
	  i.contact_id,
    i.invoice_date,
    i.invoice_due_date,
    i.line_amount_types,
    i.invoice_number,
    i.reference,
    i.branding_theme_id,
    i.url,
    i.currency_code,
    i.currency_rate,
    i.status,
    i.sent_to_contact,
    i.expected_payment_date,
    i.planned_payment_date,
    i.cis_deduction,
    i.sub_total,
    i.total_tax,
    i.total,
    i.total_discount,
    i.has_attachments,
    i.is_discounted,
    i.amount_due,
    i.amount_paid,
    i.fully_paid_on_date,
    i.amount_credited,
    i.updated_date_utc,

    li.line_item_id,
    li.description as line_item_description,
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
    li.repeating_invoice_id,

    pa.payment_id,
    pa.credit_note,
    pa.prepament,
    pa.overpayment,
    pa.credit_note_number,
    pa.account_id,
    pa.code as payment_code,
    pa.payment_date,
    pa.currency_rate as payment_currency_rate,
    pa.amount as payment_amount,
    pa.reference as payment_reference,
    pa.isReconciled,
    pa.status as payment_status,
    pa.payment_type,
    pa.updated_date_utc as payment_updated_date_utc,
    pa.bank_account_number,
    pa.particulars,
    pa.details,
    pa.has_account,

    bank_a.name as bank_account_name,
    a.name as account_name,
    co.name as contact_name

FROM invoices.invoices i
       INNER JOIN invoices.line_items li ON li.invoice_id = i.invoice_id
       LEFT JOIN invoices.payments pa ON pa.invoice_id = i.invoice_id

       LEFT JOIN accounts.accounts bank_a ON bank_a.account_id = pa.account_id
       LEFT JOIN accounts.accounts a ON a.account_code = li.account_code
       LEFT JOIN bank_transactions.contacts co ON co.contact_id = i.contact_id
