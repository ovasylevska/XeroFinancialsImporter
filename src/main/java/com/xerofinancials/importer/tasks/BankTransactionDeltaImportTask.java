package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.BankTransactions;
import com.xerofinancials.importer.beans.ImportStatistics;
import com.xerofinancials.importer.repository.BankAccountRepository;
import com.xerofinancials.importer.repository.ContactRepository;
import com.xerofinancials.importer.repository.FinancialsBankTransactionRepository;
import com.xerofinancials.importer.repository.LineItemRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class BankTransactionDeltaImportTask extends BankTransactionImportTask {
    private static final Logger logger = LoggerFactory.getLogger(BankTransactionDeltaImportTask.class);
    private static final int UNIT_DECIMAL_PLACES = 4;
    private final XeroApiWrapper xeroApiWrapper;
    private final TokenStorage tokenStorage;
    private final TaskLaunchHistoryRepository taskLaunchHistoryRepository;
    private final EmailService emailService;

    public BankTransactionDeltaImportTask(
            final XeroApiWrapper xeroApiWrapper,
            final TokenStorage tokenStorage,
            final FinancialsBankTransactionRepository bankTransactionRepository,
            final ContactRepository contactRepository,
            final BankAccountRepository bankAccountRepository,
            final LineItemRepository lineItemRepository,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final EmailService emailService
    ) {
        super(bankTransactionRepository, contactRepository, bankAccountRepository, lineItemRepository, emailService);
        this.xeroApiWrapper = xeroApiWrapper;
        this.tokenStorage = tokenStorage;
        this.taskLaunchHistoryRepository = taskLaunchHistoryRepository;
        this.emailService = emailService;
    }

    @Override
    public void execute() {
        try {
            if (!tokenStorage.isAuthentificated()) {
                throw new RuntimeException("Application is not Authenticated!");
            }
            rememberExistingData();
            processBankTransactionData();
            taskLaunchHistoryRepository.save(getDataType());
        } catch (XeroApiException e) {
            logger.error("Exception while executing task", e);
            logXeroApiException(e);
        } catch (IOException e) {
            logger.error("Exception while executing task", e);
        }
    }

    private void processBankTransactionData() throws IOException {
        final Counter pageCount = new Counter(1);
        final Counter resultsCount = new Counter(Integer.MAX_VALUE);
        final ImportStatistics importStatistics = new ImportStatistics();
        while(resultsCount.get() > 0) {
            final BankTransactions bankTransactionData = readBankTransactionData(pageCount, resultsCount);
            saveBankTransactionData(bankTransactionData);
            importStatistics.increaseNewBankTransactionsCount(bankTransactionData.getBankTransactions().size());
        }
        this.importStatistics = importStatistics;
    }

    private BankTransactions readBankTransactionData(
            final Counter pageCount,
            final Counter resultsCount
    ) throws IOException {
        logger.info("Retrieving next batch of data (page {}) ...", pageCount.get());
        final BankTransactions bankTransactionData = xeroApiWrapper.executeApiCall(
                (accountingApi, accessToken, tenantId) -> accountingApi.getBankTransactions(
                        accessToken,
                        tenantId,
                        getModifiedSinceDate(),
                        null,
                        null,
                        pageCount.get(),
                        UNIT_DECIMAL_PLACES
                ));
        resultsCount.set(bankTransactionData.getBankTransactions().size());
        pageCount.increment();
        return bankTransactionData;
    }

    private OffsetDateTime getModifiedSinceDate() {
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

    @Override
    public String getName() {
        return "Delta import of Bank Transaction data";
    }

}
