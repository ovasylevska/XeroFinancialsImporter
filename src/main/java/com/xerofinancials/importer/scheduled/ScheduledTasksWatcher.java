package com.xerofinancials.importer.scheduled;

import com.xerofinancials.importer.beans.ScheduleTime;
import com.xerofinancials.importer.repository.ScheduleRepository;
import com.xerofinancials.importer.repository.TasksRepository;
import com.xerofinancials.importer.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service
public class ScheduledTasksWatcher {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksWatcher.class);
    private static final int CHECK_INTERVAl = 60;
    private final ScheduleRepository scheduleRepository;
    private final TasksRepository tasksRepository;

    public ScheduledTasksWatcher(
            final ScheduleRepository scheduleRepository,
            final TasksRepository tasksRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.tasksRepository = tasksRepository;
        watch();
    }

    private void watch() {
        new Thread(this::watchScheduledTasks).start();
    }

    private void watchScheduledTasks() {
        while (true) {
            try {
                final Optional<ScheduleTime> scheduledTime = getScheduledTime(CHECK_INTERVAl);
                if (scheduledTime.isPresent()) {
                    final String taskIdentifier = scheduledTime.get().getTaskIdentifier();
                    final TasksRepository.ImportTaskDescription task = tasksRepository.get(TasksRepository.ImportTaskIdentifier.valueOf(taskIdentifier));
                    if (task == null) {
                        logger.info(
                                "Invalid task identifier '{}'. Skipping running task for schedule '{}'.",
                                taskIdentifier,
                                scheduledTime.get().getStartTime()
                        );
                        continue;
                    }
                    if (tasksRepository.isAnyTaskRunning(task.getImportTask().getDataType())) {
                        logger.info(
                                "Task with same data type '{}' is already running. Skipping running task '{}' for schedule '{}'.",
                                task.getImportTask().getDataType().name(),
                                task.getImportTask().getName(),
                                scheduledTime.get().getStartTime()
                        );
                        continue;
                    }
                    logger.info("Running scheduled task '{}' ...", task.getImportTask().getName());
                    task.getImportTask().run();
                }
            } catch (Exception e) {
                logger.error("Error while executing scheduled tasks", e);
            } finally {
                wait(CHECK_INTERVAl);
            }
        }
    }

    private Optional<ScheduleTime> getScheduledTime(Integer checkIntervalInSeconds) {
        final LocalTime now = DateUtils.getCurrentDateTimeInUtc().toLocalTime();
        for (final ScheduleTime scheduledTime : scheduleRepository.all()) {
            final LocalTime scheduledTimeStart = scheduledTime.getStartTime().plusSeconds(1);
            final LocalTime scheduledTimeEnd = scheduledTimeStart.plusSeconds(checkIntervalInSeconds + 1);
            if (scheduledTimeStart.equals(now)) {
                return Optional.of(scheduledTime);
            }
            if (now.isAfter(scheduledTimeStart) && now.isBefore(scheduledTimeEnd)) {
                return Optional.of(scheduledTime);
            }
        }
        return Optional.empty();
    }

    private void wait(int intervalInSeconds) {
        try {
            Thread.sleep(intervalInSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
