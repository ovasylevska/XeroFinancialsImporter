package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.dto.ContactDto;
import com.xerofinancials.importer.utils.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ContactRepository {
    private static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbc;

    public ContactRepository(@Qualifier("financialsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void clear() {
        final String sql = "TRUNCATE TABLE contacts";
        jdbc.update(sql);
    }

    public void saveNewContacts(List<ContactDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        final String sql = "INSERT INTO contacts(contact_id, name) VALUES (?, ?)";

        for (final List<ContactDto> partition : ListUtils.partitions(data, BATCH_SIZE)) {
            final Set<String> existingContactIds = getExistingContactIds(partition);
            final List<ContactDto> newContacts = partition
                    .stream()
                    .filter(c -> !existingContactIds.contains(c.getContactId()))
                    .collect(Collectors.toList());
            List<Object[]> partitionData = newContacts
                    .stream()
                    .map(c -> new Object[]{c.getContactId(), c.getName()})
                    .collect(Collectors.toList());
            jdbc.batchUpdate(sql, partitionData);
        }
    }

    private Set<String> getExistingContactIds(List<ContactDto> data) {
        final Set<String> result = new HashSet<>();
        final String sql = "SELECT contact_id " +
                "FROM contacts " +
                "WHERE contact_id IN ("
                + data
                    .stream()
                    .map(c -> "'" + c.getContactId() + "'")
                    .collect(Collectors.joining(", "))
                + ")";
        jdbc.query(sql, rs -> {
            result.add(rs.getString("contact_id"));
        });
        return result;
    }
}
