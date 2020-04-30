package com.xerofinancials.importer.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//todo: refactor bank transaction repositories
@Repository
public abstract class RollbackSupportRepository {

    public abstract JdbcTemplate getJdbc();

    public abstract String getSchema();

    public abstract String getTable();

    public void clear() {
        final String sql = "TRUNCATE TABLE " + getSchema() + "." + getTable();
        getJdbc().update(sql);
    }

    public void delete(int id) {
        final String sql = "DELETE FROM " + getSchema() + "." + getTable() + " WHERE id > ?";
        getJdbc().update(sql, id);
    }

    public Optional<Integer> getMaxEntityId() {
        final String sql = "SELECT MAX(id) AS max_entity_id FROM " + getSchema() + "." + getTable();
        final List<Integer> results = getJdbc().query(sql, (rs, rowNum) -> rs.getInt("max_entity_id"));
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }
}
