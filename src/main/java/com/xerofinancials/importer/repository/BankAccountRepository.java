package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.BankAccountDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BankAccountRepository extends RollbackSupportRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public BankAccountRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
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
        return "bank_accounts";
    }

    public void saveNewBankAccounts(List<BankAccountDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO bank_transactions.bank_accounts(bank_account_id, name, code, unique_hash) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT DO NOTHING";

        for (final List<BankAccountDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {
            List<Object[]> partitionData = partition
                    .stream()
                    .map(b -> new Object[]{b.getBankAccountId(), b.getName(), b.getCode(), b.getUniqueHash()})
                    .collect(Collectors.toList());
            jdbc.batchUpdate(sql, partitionData);
        }
    }
}
