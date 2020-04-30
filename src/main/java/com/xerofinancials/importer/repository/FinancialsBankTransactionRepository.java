package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.BankTransactionDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FinancialsBankTransactionRepository extends RollbackSupportRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public FinancialsBankTransactionRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
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
        return "bank_transactions";
    }

    public void save(List<BankTransactionDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO bank_transactions.bank_transactions(" +
                "bank_transaction_id, " +
                "bank_transaction_type, " +
                "contact_id, " +
                "bank_account_id, " +
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
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (final List<BankTransactionDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {

            final List<Object[]> partitionData = partition
                    .stream()
                    .map(t -> new Object[]{
                            t.getBankTransactionId(),
                            t.getType(),
                            t.getContactId(),
                            t.getBankAccountId(),
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
}
