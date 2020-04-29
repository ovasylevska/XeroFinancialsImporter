package com.xerofinancials.importer.tasks;

import com.xero.models.accounting.Accounts;
import com.xerofinancials.importer.repository.AccountRepository;
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
public class AccountDeltaImportTask extends AccountImportTask {
    private static final Logger logger = LoggerFactory.getLogger(AccountDeltaImportTask.class);
    private final XeroApiWrapper xeroApiWrapper;

    protected AccountDeltaImportTask(
            final EmailService emailService,
            final XeroApiWrapper xeroApiWrapper,
            final AccountRepository accountRepository,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository
    ) {
        super(emailService, xeroApiWrapper, accountRepository, tokenStorage, taskLaunchHistoryRepository);
        this.xeroApiWrapper = xeroApiWrapper;
    }

    @Override
    public String getName() {
        return "Delta import of Account data";
    }

    @Override
    protected Accounts readAccountData() throws IOException {
        final OffsetDateTime modifiedSinceDate = getModifiedSinceDate();
        logger.info("Retrieving data since {} ...", modifiedSinceDate);
        return xeroApiWrapper.executeApiCall(
                (accountingApi, accessToken, tenantId) ->
                        accountingApi.getAccounts(
                                accessToken,
                                tenantId,
                                modifiedSinceDate,
                                null,
                                null
                        )
        );
    }
}
