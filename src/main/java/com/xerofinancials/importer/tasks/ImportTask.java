package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.Element;
import com.xero.models.accounting.ValidationError;
import com.xerofinancials.importer.beans.ImportStatistics;
import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ImportTask {
    private static final Logger logger = LoggerFactory.getLogger(ImportTask.class);
    protected ImportStatistics importStatistics;
    private final EmailService emailService;
    private boolean isRunning = false;

    protected ImportTask(final EmailService emailService) {
        this.emailService = emailService;
    }

    protected abstract void execute();

    public abstract String getName();

    public abstract XeroDataType getDataType();

    public void run() {
        try {
            logger.info("Starting '{}' task ...", getName());
            this.isRunning = true;
            execute();
            logger.info("Task '{}' is finished.", getName());

            if (importStatistics != null) {
                emailService.sendNotificationEmail(
                        "Task execution completed",
                        "Task '" + getName() + "' is completed. " + importStatistics.toString()
                );
            }
        } catch (Exception e) {
            logger.error("Exception while executing task '{}'.", getName());
            logger.error(e.getMessage(), e);
            emailService.sendErrorEmail(
                    "Exception while executing task",
                    "Task '" + getName() + "' is failed with error : " + e.getMessage()
            );
        } finally {
            this.isRunning = false;
        }
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    void logXeroApiException(XeroApiException e) {
        logger.error("Xero Api Exception: " + e.getResponseCode());
        for (Element item : e.getError().getElements()) {
            for (ValidationError err : item.getValidationErrors()) {
                logger.error("Validation error : " + err.getMessage());
            }
        }
    }

    static class Counter {
        int count;

        Counter(int count) {
            this.count = count;
        }

        void increment() {
            this.count++;
        }

        int get() {
            return this.count;
        }

        void set(int count) {
            this.count = count;
        }
    }
}
