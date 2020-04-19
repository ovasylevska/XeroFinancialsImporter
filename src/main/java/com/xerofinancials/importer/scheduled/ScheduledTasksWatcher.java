package com.xerofinancials.importer.scheduled;

import com.xerofinancials.importer.tasks.TestImportTask;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasksWatcher {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksWatcher.class);
    private final XeroApiWrapper xeroApiWrapper;
    private final TokenStorage tokenStorage;

    public ScheduledTasksWatcher(
            final XeroApiWrapper xeroApiWrapper,
            final TokenStorage tokenStorage
    ) {
        this.xeroApiWrapper = xeroApiWrapper;
        this.tokenStorage = tokenStorage;
        watch();
    }

    private void watch() {
        new Thread(this::watchScheduledTasks).start();
    }

    private void watchScheduledTasks() {
        while (true) {
            try {
                TestImportTask task = new TestImportTask(xeroApiWrapper, tokenStorage);
                task.execute();
            } catch (Exception e) {
                logger.error("Error while executing scheduled tasks", e);
            } finally {
                int interval = 20 * 60; //20 min
                wait(interval);
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
