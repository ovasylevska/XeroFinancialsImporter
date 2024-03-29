package com.xerofinancials.importer.tasks;

import com.xero.models.accounting.BankTransactions;
import com.xerofinancials.importer.repository.BankAccountRepository;
import com.xerofinancials.importer.repository.ContactRepository;
import com.xerofinancials.importer.repository.BankTransactionRepository;
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

    public BankTransactionInitialImportTask(
            final XeroApiWrapper xeroApiWrapper,
            final TokenStorage tokenStorage,
            final BankTransactionRepository bankTransactionRepository,
            final ContactRepository contactRepository,
            final BankAccountRepository bankAccountRepository,
            final LineItemRepository lineItemRepository,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final EmailService emailService
    ) {
        super(
                taskLaunchHistoryRepository,
                tokenStorage,
                bankTransactionRepository,
                contactRepository,
                bankAccountRepository,
                lineItemRepository,
                emailService
        );
        this.xeroApiWrapper = xeroApiWrapper;
    }

    @Override
    public String getName() {
        return "Initial import of Bank Transaction data";
    }

    protected BankTransactions readBankTransactionData(
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
                        "Date",
                        pageCount.get(),
                        UNIT_DECIMAL_PLACES
                ));
        return bankTransactionData;
    }
}
