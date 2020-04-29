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

import java.io.IOException;

@Component
public class AccountInitialImportTask extends AccountImportTask {
    private static final Logger logger = LoggerFactory.getLogger(AccountInitialImportTask.class);
    private final XeroApiWrapper xeroApiWrapper;

    protected AccountInitialImportTask(
            final EmailService emailService,
            final XeroApiWrapper xeroApiWrapper,
            final AccountRepository accountRepository,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final TokenStorage tokenStorage
    ) {
        super(emailService, xeroApiWrapper, accountRepository, tokenStorage, taskLaunchHistoryRepository);
        this.xeroApiWrapper = xeroApiWrapper;
    }

    @Override
    public String getName() {
        return "Initial import of Account data";
    }

    @Override
    protected Accounts readAccountData() throws IOException {
        logger.info("Retrieving all data ...");
        return xeroApiWrapper.executeApiCall(
                (accountingApi, accessToken, tenantId) ->
                        accountingApi.getAccounts(accessToken, tenantId, null, null, null));

    }
}
