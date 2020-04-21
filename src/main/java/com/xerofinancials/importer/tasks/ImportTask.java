package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.Element;
import com.xero.models.accounting.ValidationError;
import com.xerofinancials.importer.enums.XeroDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ImportTask {
    private static final Logger logger = LoggerFactory.getLogger(ImportTask.class);
    private boolean isRunning = false;

    protected abstract void execute();

    public abstract String getName();

    public abstract XeroDataType getDataType();

    public void run() {
        try {
            logger.info("Starting '{}' task ...", getName());
            this.isRunning = true;
            execute();
            logger.info("Task '{}' is finished.", getName());
        } catch (Exception e) {
            logger.error("Exception while executing task '{}'.", getName());
            logger.error(e.getMessage(), e);
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
