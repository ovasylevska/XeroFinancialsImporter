package com.xerofinancials.importer.tasks;

import com.xerofinancials.importer.repository.BankAccountRepository;
import com.xerofinancials.importer.repository.ContactRepository;
import com.xerofinancials.importer.repository.FinancialsBankTransactionRepository;
import com.xerofinancials.importer.repository.LineItemRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BankTransactionReconciliationTask extends BankTransactionImportTask {
    private static final Logger logger = LoggerFactory.getLogger(BankTransactionReconciliationTask.class);
    private final BankTransactionInitialImportTask initialImportTask;
    private final TokenStorage tokenStorage;

    public BankTransactionReconciliationTask(
            final FinancialsBankTransactionRepository bankTransactionRepository,
            final ContactRepository contactRepository,
            final BankAccountRepository bankAccountRepository,
            final LineItemRepository lineItemRepository,
            final BankTransactionInitialImportTask initialImportTask,
            final TokenStorage tokenStorage,
            final EmailService emailService,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository) {
        super(
                taskLaunchHistoryRepository,
                tokenStorage,
                bankTransactionRepository,
                contactRepository,
                bankAccountRepository,
                lineItemRepository,
                emailService
        );
        this.initialImportTask = initialImportTask;
        this.tokenStorage = tokenStorage;
    }

    @Override
    public void execute() {
        if (!tokenStorage.isAuthentificated()) {
            throw new RuntimeException("Application is not Authenticated!");
        }

        logger.info("Deleting all Bank Transaction data from database...");
        bankTransactionRepository.clear();

        logger.info("Deleting all Contacts data from database...");
        contactRepository.clear();

        logger.info("Deleting all Bank Account data from database...");
        bankAccountRepository.clear();

        logger.info("Deleting all Line Items data from database...");
        lineItemRepository.clear();

        logger.info("Launching task '{}'...", initialImportTask.getName());
        initialImportTask.run();

        this.importStatistics = initialImportTask.importStatistics;
    }

    @Override
    public String getName() {
        return "Reconciliation of Bank Transaction data";
    }

}
