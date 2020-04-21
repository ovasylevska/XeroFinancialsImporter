package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.beans.ScheduleTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Repository
public class ScheduleRepository {
    private final JdbcTemplate jdbc;

    public ScheduleRepository(@Qualifier("importerJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<ScheduleTime> get(Integer id) {
        final String sql = "SELECT id, start_time " +
                "FROM schedule " +
                "WHERE id = ?";
        final List<ScheduleTime> result = jdbc.query(sql, (rs, rowNum) -> getScheduleTime(rs), id);
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    public List<ScheduleTime> all() {
        final String sql = "SELECT id, start_time FROM schedule";
        return jdbc.query(sql, (rs, rowNum) -> getScheduleTime(rs));
    }

    public void delete(Integer id) {
        final String sql = "DELETE FROM schedule WHERE id = ?";
        jdbc.update(sql, id);
    }

    public void save(ScheduleTime scheduleTime) {
        final String sql = "INSERT INTO schedule(start_time) VALUES (?)";
        jdbc.update(sql, scheduleTime.getStartTime());
    }

    private ScheduleTime getScheduleTime(final ResultSet rs) throws SQLException {
        final ScheduleTime scheduleTime = new ScheduleTime();
        scheduleTime.setId(rs.getInt("id"));
        final Time startTime = rs.getTime("start_time");
        if (startTime != null) {
            scheduleTime.setStartTime(startTime.toLocalTime());
        }
        return scheduleTime;
    }

}
