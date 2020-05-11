package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.ContactDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ContactRepository extends RollbackSupportRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public ContactRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
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
        return "contacts";
    }

    public void saveNewContacts(List<ContactDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO bank_transactions.contacts(contact_id, name, unique_hash) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT DO NOTHING";

        for (final List<ContactDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {
            List<Object[]> partitionData = partition
                    .stream()
                    .map(c -> new Object[]{c.getContactId(), c.getName(), c.getUniqueHash()})
                    .collect(Collectors.toList());
            jdbc.batchUpdate(sql, partitionData);
        }
    }
}
