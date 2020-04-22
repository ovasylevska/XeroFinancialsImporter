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

import java.io.IOException;

@Component
public class BankTransactionInitialImportTask extends BankTransactionImportTask {
    private static final Logger logger = LoggerFactory.getLogger(BankTransactionInitialImportTask.class);
    private static final int UNIT_DECIMAL_PLACES = 4;
    private final XeroApiWrapper xeroApiWrapper;
    private final TokenStorage tokenStorage;
    private final TaskLaunchHistoryRepository taskLaunchHistoryRepository;
    private final EmailService emailService;

    public BankTransactionInitialImportTask(
            final XeroApiWrapper xeroApiWrapper,
            final TokenStorage tokenStorage,
            final FinancialsBankTransactionRepository bankTransactionRepository,
            final ContactRepository contactRepository,
            final BankAccountRepository bankAccountRepository,
            final LineItemRepository lineItemRepository,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final EmailService emailService) {
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
            logXeroApiException(e);
            throw new RuntimeException("Failed to execute '" + getName() + "' task", e);
        } catch (Exception e) {
            logger.error("Exception while executing task", e);
            throw new RuntimeException("Failed to execute '" + getName() + "' task", e);
        }
    }

    @Override
    public String getName() {
        return "Initial import of Bank Transaction data";
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
                        null,
                        null,
                        null,
                        pageCount.get(),
                        UNIT_DECIMAL_PLACES
                ));
        resultsCount.set(bankTransactionData.getBankTransactions().size());
        pageCount.increment();
        return bankTransactionData;
    }
}
