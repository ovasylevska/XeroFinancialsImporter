package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.Element;
import com.xero.models.accounting.ValidationError;
import com.xerofinancials.importer.beans.ImportStatistics;
import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.exceptions.DoNotRollbackException;
import com.xerofinancials.importer.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            sendStatisticsEmail();
        } catch (DoNotRollbackException ne) {
            logger.error("Exception while executing task '{}' : {}", getName(), ne.getException().getMessage());
            sendErrorEmail(ne.getException());
        } catch (Exception e) {
            logger.error("Exception while executing task '{}' : {}", getName(), e.getMessage());
            rollback();
            sendErrorEmail(e, "All new imported data will be rolled back");
        } finally {
            this.isRunning = false;
        }
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    private void sendStatisticsEmail() {
        if (importStatistics != null) {
            emailService.sendNotificationEmail(
                    "Task execution completed",
                    Arrays.asList("Task '" + getName() + "' is completed. " + importStatistics.toString())
            );
        }
    }

    private void sendErrorEmail(Exception e, String... errorMessages) {
        final List<String> errorMessagesList = Arrays.stream(errorMessages).collect(Collectors.toList());
        errorMessagesList.add("Task '" + getName() + "' is failed with error : " + e.getMessage());
        errorMessagesList.add(getStackTrace(e));
        emailService.sendErrorEmail(
                "Exception while executing task",
                errorMessagesList
        );
    }

    private void sendErrorEmail(Exception e) {
        emailService.sendErrorEmail(
                "Exception while executing task",
                Arrays.asList(
                        "Task '" + getName() + "' is failed with error : " + e.getMessage(),
                        getStackTrace(e)
                )
        );
    }

    void logXeroApiException(XeroApiException e) {
        logger.error("Xero Api Exception: " + e.getResponseCode());
        if (e.getError() == null) {
            return;
        }
        for (Element item : e.getError().getElements()) {
            for (ValidationError err : item.getValidationErrors()) {
                logger.error("Validation error : " + err.getMessage());
            }
        }
    }

    void rollback() {

    }

    private String getStackTrace(Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
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
