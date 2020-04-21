package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.BankAccountDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class BankAccountRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public BankAccountRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void clear() {
        final String sql = "TRUNCATE TABLE bank_accounts";
        jdbc.update(sql);
    }

    public void saveNewBankAccounts(List<BankAccountDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO bank_accounts(bank_account_id, name, code) VALUES (?, ?, ?)";

        for (final List<BankAccountDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {
            final Set<String> existingBankAccountIds = getExistingBankAccountIds(partition);
            final List<BankAccountDto> newBankAccounts = partition
                    .stream()
                    .filter(b -> !existingBankAccountIds.contains(b.getBankAccountId()))
                    .collect(Collectors.toList());
            List<Object[]> partitionData = newBankAccounts
                    .stream()
                    .map(b -> new Object[]{b.getBankAccountId(), b.getName(), b.getCode()})
                    .collect(Collectors.toList());
            jdbc.batchUpdate(sql, partitionData);
        }
    }

    public void delete(int id) {
        final String sql = "DELETE FROM bank_accounts WHERE id > ?";
        jdbc.update(sql, id);
    }

    public Optional<Integer> getMaxEntityId() {
        final String sql = "SELECT MAX(id) AS max_entity_id FROM bank_accounts";
        final List<Integer> results = jdbc.query(sql, (rs, rowNum) -> rs.getInt("max_entity_id"));
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    private Set<String> getExistingBankAccountIds(List<BankAccountDto> data) {
        final Set<String> result = new HashSet<>();
        final String sql = "SELECT bank_account_id " +
                "FROM bank_accounts " +
                "WHERE bank_account_id IN ("
                + data
                .stream()
                .map(b -> "'" + b.getBankAccountId() + "'")
                .collect(Collectors.joining(", "))
                + ")";
        jdbc.query(sql, rs -> {
            result.add(rs.getString("bank_account_id"));
        });
        return result;
    }
}
