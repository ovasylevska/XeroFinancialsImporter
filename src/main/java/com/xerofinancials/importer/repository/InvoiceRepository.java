package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.InvoiceDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InvoiceRepository extends RollbackSupportRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public InvoiceRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public JdbcTemplate getJdbc() {
        return this.jdbc;
    }

    @Override
    public String getSchema() {
        return "invoices";
    }

    @Override
    public String getTable() {
        return "invoices";
    }

    public void save(final List<InvoiceDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO invoices.invoices(" +
                "invoice_id, " +
                "invoice_type, " +
                "contact_id, " +
                "invoice_date, " +
                "invoice_due_date, " +
                "line_amount_types, " +
                "invoice_number, " +
                "reference, " +
                "branding_theme_id, " +
                "url, " +
                "currency_code, " +
                "currency_rate, " +
                "status, " +
                "sent_to_contact, " +
                "expected_payment_date, " +
                "planned_payment_date, " +
                "cis_deduction, " +
                "sub_total, " +
                "total_tax, " +
                "total, " +
                "total_discount, " +
                "has_attachments, " +
                "is_discounted, " +
                "amount_due, " +
                "amount_paid, " +
                "fully_paid_on_date, " +
                "amount_credited, " +
                "updated_date_utc, " +
                "status_attribute_string, " +
                "unique_hash " +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT DO NOTHING";
        for (final List<InvoiceDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {
            List<Object[]> partitionData = partition
                    .stream()
                    .map(i -> new Object[]{
                            i.getInvoiceID(),
                            i.getInvoiceType(),
                            i.getContactId(),
                            i.getDate(),
                            i.getDueDate(),
                            i.getLineAmountTypes(),
                            i.getInvoiceNumber(),
                            i.getReference(),
                            i.getBrandingThemeID(),
                            i.getUrl(),
                            i.getCurrencyCode(),
                            i.getCurrencyRate(),
                            i.getStatus(),
                            i.getSentToContact(),
                            i.getExpectedPaymentDate(),
                            i.getPlannedPaymentDate(),
                            i.getCiSDeduction(),
                            i.getSubTotal(),
                            i.getTotalTax(),
                            i.getTotal(),
                            i.getTotalDiscount(),
                            i.getHasAttachments(),
                            i.getIsDiscounted(),
                            i.getAmountDue(),
                            i.getAmountPaid(),
                            i.getFullyPaidOnDate(),
                            i.getAmountCredited(),
                            i.getUpdatedDateUTC(),
                            i.getStatusAttributeString(),
                            i.getUniqueHash()
                    })
                    .collect(Collectors.toList());
            jdbc.batchUpdate(sql, partitionData);
        }
    }
}
