package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.AccountDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AccountRepository extends RollbackSupportRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public AccountRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public JdbcTemplate getJdbc() {
        return jdbc;
    }

    @Override
    public String getSchema() {
        return "accounts";
    }

    @Override
    public String getTable() {
        return "accounts";
    }

    public void save(List<AccountDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO accounts.accounts(" +
                "account_id, " +
                "account_code, " +
                "name, " +
                "type, " +
                "bank_account_number, " +
                "status, " +
                "description, " +
                "bank_account_type, " +
                "currency_code, " +
                "tax_type, " +
                "enable_payments_to_account, " +
                "show_in_expense_claims, " +
                "property_class, " +
                "system_account, " +
                "reporting_code, " +
                "reporting_code_name, " +
                "has_attachments, " +
                "updated_date_utc, " +
                "add_to_watch_list " +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (final List<AccountDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {

            final List<Object[]> partitionData = partition
                    .stream()
                    .map(t -> new Object[]{
                            t.getAccountId(),
                            t.getAccountCode(),
                            t.getName(),
                            t.getType(),
                            t.getBankAccountNumber(),
                            t.getStatus(),
                            t.getDescription(),
                            t.getBankAccountType(),
                            t.getCurrencyCode(),
                            t.getTaxType(),
                            t.getEnablePaymentsToAccount(),
                            t.getShowInExpenseClaims(),
                            t.getPropertyClass(),
                            t.getSystemAccount(),
                            t.getReportingCode(),
                            t.getReportingCodeName(),
                            t.getHasAttachments(),
                            t.getUpdatedDateUtc(),
                            t.getAddToWatchList()
                    })
                    .collect(Collectors.toList());

            jdbc.batchUpdate(sql, partitionData);
        }
    }


}
