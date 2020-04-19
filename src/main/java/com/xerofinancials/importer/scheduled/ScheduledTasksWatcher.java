package com.xerofinancials.importer.scheduled;

import com.xerofinancials.importer.tasks.TestImportTask;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasksWatcher {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksWatcher.class);
    private final XeroApiWrapper xeroApiWrapper;

    public ScheduledTasksWatcher(final XeroApiWrapper xeroApiWrapper) {
        this.xeroApiWrapper = xeroApiWrapper;
        watch();
    }

    private void watch() {
        new Thread(this::watchScheduledTasks).start();
    }

    private void watchScheduledTasks() {
        while (true) {
            try {
                TestImportTask task = new TestImportTask(xeroApiWrapper);
                task.execute();

                int interval = 20 * 60; //20 min
                wait(interval);
            } catch (Exception e) {
                logger.error("Error while executing scheduled tasks", e);
            }
        }
    }

    private void wait(int intervalInSeconds) {
        try {
            Thread.sleep(intervalInSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
