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
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;

@Component
public class BankTransactionDeltaImportTask extends BankTransactionImportTask {
    private static final Logger logger = LoggerFactory.getLogger(BankTransactionDeltaImportTask.class);
    private static final int UNIT_DECIMAL_PLACES = 4;
    private final XeroApiWrapper xeroApiWrapper;

    public BankTransactionDeltaImportTask(
            final XeroApiWrapper xeroApiWrapper,
            final TokenStorage tokenStorage,
            final BankTransactionRepository bankTransactionRepository,
            final ContactRepository contactRepository,
            final BankAccountRepository bankAccountRepository,
            final LineItemRepository lineItemRepository,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final EmailService emailService
    ) {
        super(taskLaunchHistoryRepository, tokenStorage, bankTransactionRepository, contactRepository, bankAccountRepository, lineItemRepository, emailService);
        this.xeroApiWrapper = xeroApiWrapper;
    }

    protected BankTransactions readBankTransactionData(
            final Counter pageCount,
            final Counter resultsCount
    ) throws IOException {
        final OffsetDateTime modifiedSinceDate = getModifiedSinceDate();
        logger.info("Retrieving next batch of data (page {}) since {} ...", pageCount.get(), modifiedSinceDate);
        final BankTransactions bankTransactionData = xeroApiWrapper.executeApiCall(
                (accountingApi, accessToken, tenantId) -> accountingApi.getBankTransactions(
                        accessToken,
                        tenantId,
                        modifiedSinceDate,
                        null,
                        "Date",
                        pageCount.get(),
                        UNIT_DECIMAL_PLACES
                ));
        return bankTransactionData;
    }

    @Override
    public String getName() {
        return "Delta import of Bank Transaction data";
    }

}
