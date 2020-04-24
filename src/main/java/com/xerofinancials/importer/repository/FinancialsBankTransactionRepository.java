package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.BankTransactionDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FinancialsBankTransactionRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public FinancialsBankTransactionRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void clear() {
        final String sql = "TRUNCATE TABLE bank_transactions";
        jdbc.update(sql);
    }

    public void save(List<BankTransactionDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO bank_transactions(" +
                "bank_transaction_id, " +
                "bank_transaction_type, " +
                "contact_id, " +
                "bank_account_id, " +
                "line_items, " + //todo: check if needed
                "is_reconciled, " +
                "bank_transaction_date, " +
                "reference, " +
                "currency_code, " +
                "currency_rate, " +
                "url, " +
                "status, " +
                "line_amount_types, " +
                "sub_total, " +
                "total_tax, " +
                "total, " +
                "prepayment_id, " +
                "overpayment_id, " +
                "updated_date_utc, " +
                "has_attachments, " +
                "status_attribute_string " +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (final List<BankTransactionDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {

            final List<Object[]> partitionData = partition
                    .stream()
                    .map(t -> new Object[]{
                            t.getBankTransactionId(),
                            t.getType(),
                            t.getContactId(),
                            t.getBankAccountId(),
                            t.getLineItems(),
                            t.getIsReconciled(),
                            t.getDate(),
                            t.getReference(),
                            t.getCurrencyCode(),
                            t.getCurrencyRate(),
                            t.getUrl(),
                            t.getStatus(),
                            t.getLineAmountTypes(),
                            t.getSubTotal(),
                            t.getTotalTax(),
                            t.getTotal(),
                            t.getPrepaymentId(),
                            t.getOverpaymentId(),
                            t.getUpdatedDateUTC(),
                            t.getHasAttachments(),
                            t.getStatusAttributeString()
                    })
                    .collect(Collectors.toList());

            jdbc.batchUpdate(sql, partitionData);
        }
    }

    public void delete(int id) {
        final String sql = "DELETE FROM bank_transactions WHERE id > ?";
        jdbc.update(sql, id);
    }

    public Optional<Integer> getMaxEntityId() {
        final String sql = "SELECT MAX(id) AS max_entity_id FROM bank_transactions";
        final List<Integer> results = jdbc.query(sql, (rs, rowNum) -> rs.getInt("max_entity_id"));
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

}
