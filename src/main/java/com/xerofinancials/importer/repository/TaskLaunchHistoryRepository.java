package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.utils.DateUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskLaunchHistoryRepository {
    private final JdbcTemplate jdbc;

    public TaskLaunchHistoryRepository(@Qualifier("importerJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(XeroDataType dataType) {
        clear(dataType);
        final String sql = "INSERT INTO task_launch_history(data_type, last_launch_time) VALUES (?, ?)";
        jdbc.update(sql, dataType.name(), DateUtils.getCurrentDateTimeInUtc());
    }

    public void clear(XeroDataType dataType) {
        final String sql = "DELETE FROM task_launch_history WHERE data_type = ?";
        jdbc.update(sql, dataType.name());
    }

    public Optional<LocalDateTime> get(XeroDataType dataType) {
        final String sql = "SELECT last_launch_time FROM task_launch_history WHERE data_type = ?";
        List<LocalDateTime> result = jdbc.query(sql, (rs, rowNum) -> {
            Timestamp lastLaunchTime = rs.getTimestamp("last_launch_time");
            return lastLaunchTime.toLocalDateTime();
        }, dataType.name());
        if(result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

}
