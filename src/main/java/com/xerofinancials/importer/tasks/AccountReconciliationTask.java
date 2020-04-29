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

@Component
public class AccountReconciliationTask extends AccountImportTask {
    private static final Logger logger = LoggerFactory.getLogger(AccountReconciliationTask.class);
    private final AccountInitialImportTask initialImportTask;

    protected AccountReconciliationTask(
            final EmailService emailService,
            final XeroApiWrapper xeroApiWrapper,
            final AccountRepository accountRepository,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final AccountInitialImportTask initialImportTask
    ) {
        super(emailService, xeroApiWrapper, accountRepository, tokenStorage, taskLaunchHistoryRepository);
        this.initialImportTask = initialImportTask;
    }

    @Override
    public String getName() {
        return "Reconciliation of Account data";
    }

    @Override
    protected Accounts readAccountData() {
        return null;
    }

    @Override
    public void execute() {
        logger.info("Deleting all Account data from database...");
        accountRepository.clear();

        logger.info("Launching task '{}'...", initialImportTask.getName());
        initialImportTask.run();

        this.importStatistics = initialImportTask.importStatistics;
    }
}
