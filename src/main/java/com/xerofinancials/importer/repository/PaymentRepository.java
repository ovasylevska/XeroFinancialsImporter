package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.PaymentDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PaymentRepository extends RollbackSupportRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public PaymentRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
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
        return "payments";
    }

    public void save(final List<PaymentDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO invoices.payments(" +
                "payment_id, " +
                "invoice_id, " +
                "credit_note , " +
                "prepament , " +
                "overpayment , " +
                "invoice_number, " +
                "credit_note_number, " +
                "account_id, " +
                "code, " +
                "payment_date, " +
                "currency_rate, " +
                "amount, " +
                "reference, " +
                "isReconciled, " +
                "status, " +
                "payment_type, " +
                "updated_date_utc, " +
                "bank_account_number, " +
                "particulars, " +
                "details, " +
                "has_account " +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        for (final List<PaymentDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {
            List<Object[]> partitionData = partition
                    .stream()
                    .map(p -> new Object[]{
                            p.getPaymentId(),
                            p.getInvoiceId(),
                            p.getCreditNote(),
                            p.getPrepayment(),
                            p.getOverpayment(),
                            p.getInvoiceNumber(),
                            p.getCreditNoteNumber(),
                            p.getAccountId(),
                            p.getCode(),
                            p.getPaymentDate(),
                            p.getCurrencyRate(),
                            p.getAmount(),
                            p.getReference(),
                            p.getIsReconciled(),
                            p.getStatus(),
                            p.getPaymentType(),
                            p.getUpdatedDateUTC(),
                            p.getBankAccountNumber(),
                            p.getParticulars(),
                            p.getDetails(),
                            p.getHasAccount()
                    })
                    .collect(Collectors.toList());

            jdbc.batchUpdate(sql, partitionData);
        }
    }
}
