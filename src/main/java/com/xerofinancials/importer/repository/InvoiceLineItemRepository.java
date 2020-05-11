package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.LineItemDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InvoiceLineItemRepository extends LineItemRepository {
    private static final int BATCH_SIZE = 1000;

    public InvoiceLineItemRepository(@Qualifier("financialsJdbcTemplate") final JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    public String getSchema() {
        return "invoices";
    }

    @Override
    public String getTable() {
        return "line_items";
    }

    public void save(List<LineItemDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO invoices.line_items(" +
                "line_item_id, " +
                "invoice_id, " +
                "description, " +
                "quantity, " +
                "unit_amount, " +
                "item_code, " +
                "account_code, " +
                "tax_type, " +
                "tax_amount, " +
                "line_amount, " +
                "tracking, " +
                "discount_rate, " +
                "discount_amount, " +
                "repeating_invoice_id, " +
                "unique_hash " +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT DO NOTHING";

        for (final List<LineItemDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {
            List<Object[]> partitionData = partition
                    .stream()
                    .map(l -> new Object[]{
                            l.getLineItemId(),
                            l.getInvoiceId(),
                            l.getDescription(),
                            l.getQuantity(),
                            l.getUnitAmount(),
                            l.getItemCode(),
                            l.getAccountCode(),
                            l.getTaxType(),
                            l.getTaxAmount(),
                            l.getLineAmount(),
                            l.getTracking(),
                            l.getDiscountRate(),
                            l.getDiscountAmount(),
                            l.getRepeatingInvoiceID(),
                            l.getUniqueHash()
                    })
                    .collect(Collectors.toList());
            getJdbc().batchUpdate(sql, partitionData);
        }
    }
}
