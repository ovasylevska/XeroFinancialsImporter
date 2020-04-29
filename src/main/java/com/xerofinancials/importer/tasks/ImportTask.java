package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.Element;
import com.xero.models.accounting.ValidationError;
import com.xerofinancials.importer.beans.ImportStatistics;
import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.exceptions.DoNotRollbackException;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ImportTask {
    private static final Logger logger = LoggerFactory.getLogger(ImportTask.class);
    private final EmailService emailService;
    private final TokenStorage tokenStorage;
    private final TaskLaunchHistoryRepository taskLaunchHistoryRepository;
    private boolean isRunning = false;
    ImportStatistics importStatistics;

    protected ImportTask(
            final EmailService emailService,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository
    ) {
        this.emailService = emailService;
        this.tokenStorage = tokenStorage;
        this.taskLaunchHistoryRepository = taskLaunchHistoryRepository;
    }

    protected abstract void execute();

    public abstract String getName();

    public abstract XeroDataType getDataType();

    public boolean getIsRunning() {
        return isRunning;
    }

    public void run() {
        try {
            logger.info("Starting task '{}' ...", getName());
            this.isRunning = true;
            validateIfAuthentificated();
            execute();
            logger.info("Task '{}' is finished.", getName());
            taskLaunchHistoryRepository.save(getDataType());
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

    OffsetDateTime getModifiedSinceDate() {
        final Optional<LocalDateTime> lastLaunchTimeOptional = taskLaunchHistoryRepository.get(getDataType());
        if (!lastLaunchTimeOptional.isPresent()) {
            return null;
        } else {
            final LocalDateTime lastLaunchTime = lastLaunchTimeOptional.get();
            final org.threeten.bp.LocalDateTime localDateTime = org.threeten.bp.LocalDateTime.of(
                    lastLaunchTime.getYear(),
                    lastLaunchTime.getMonthValue(),
                    lastLaunchTime.getDayOfMonth(),
                    lastLaunchTime.getHour(),
                    lastLaunchTime.getMinute(),
                    lastLaunchTime.getSecond()
            );
            return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
        }
    }

    //todo: add rollback with temp tables
    void rollback() {

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

    private void validateIfAuthentificated() {
        if (!tokenStorage.isAuthentificated()) {
            throw new RuntimeException("Application is not Authenticated!");
        }
    }

    private void sendStatisticsEmail() {
        if (importStatistics != null) {
            emailService.sendNotificationEmail(
                    "Task execution completed",
                    Collections.singletonList("Task '" + getName() + "' is completed. " + importStatistics.toString())
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
