package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.LineItemDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LineItemRepository extends RollbackSupportRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public LineItemRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public JdbcTemplate getJdbc() {
        return this.jdbc;
    }

    @Override
    public String getSchema() {
        return "bank_transactions";
    }

    @Override
    public String getTable() {
        return "line_items";
    }

    public void save(List<LineItemDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO bank_transactions.line_items(" +
                "line_item_id, " +
                "bank_transaction_id, " +
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
                "repeating_invoice_id" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (final List<LineItemDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {
            List<Object[]> partitionData = partition
                    .stream()
                    .map(l -> new Object[]{
                            l.getLineItemId(),
                            l.getBankTransactionId(),
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
                            l.getRepeatingInvoiceID()
                    })
                    .collect(Collectors.toList());
            jdbc.batchUpdate(sql, partitionData);
        }
    }

}
