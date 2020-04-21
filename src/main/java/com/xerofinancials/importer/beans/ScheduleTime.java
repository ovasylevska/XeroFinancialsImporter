package com.xerofinancials.importer.beans;

import java.time.LocalTime;
import java.util.Objects;

public class ScheduleTime {
    private Integer id;
    private LocalTime startTime;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(final LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ScheduleTime that = (ScheduleTime) o;
        return Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime);
    }
}
