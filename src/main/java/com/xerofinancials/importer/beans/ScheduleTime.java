package com.xerofinancials.importer.beans;

import java.time.LocalTime;
import java.util.Objects;

public class ScheduleTime {
    private Integer id;
    private LocalTime startTime;
    private String taskIdentifier;
    private String taskDescription;

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

    public String getTaskIdentifier() {
        return taskIdentifier;
    }

    public void setTaskIdentifier(final String taskIdentifier) {
        this.taskIdentifier = taskIdentifier;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(final String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ScheduleTime that = (ScheduleTime) o;
        return Objects.equals(startTime, that.startTime) &&
                Objects.equals(taskIdentifier, that.taskIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, taskIdentifier);
    }
}
