package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.LineItemDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class LineItemRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public LineItemRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void clear() {
        final String sql = "TRUNCATE TABLE line_items";
        jdbc.update(sql);
    }

    public void save(List<LineItemDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO line_items(" +
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

    public void delete(int id) {
        final String sql = "DELETE FROM line_items WHERE id > ?";
        jdbc.update(sql, id);
    }

    public Optional<Integer> getMaxEntityId() {
        final String sql = "SELECT MAX(id) AS max_entity_id FROM line_items";
        final List<Integer> results = jdbc.query(sql, (rs, rowNum) -> rs.getInt("max_entity_id"));
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

}
