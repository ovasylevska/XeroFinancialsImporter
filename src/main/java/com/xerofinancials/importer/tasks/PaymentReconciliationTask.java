package com.xerofinancials.importer.tasks;

import com.xero.models.accounting.Payments;
import com.xerofinancials.importer.repository.PaymentRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PaymentReconciliationTask extends PaymentImportTask {
    private static final Logger logger = LoggerFactory.getLogger(PaymentImportTask.class);
    private final PaymentInitialImportTask initialImportTask;
    private final PaymentRepository paymentRepository;

    protected PaymentReconciliationTask(
            final EmailService emailService,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final PaymentRepository paymentRepository,
            final PaymentInitialImportTask initialImportTask
    ) {
        super(emailService, tokenStorage, taskLaunchHistoryRepository, paymentRepository);
        this.initialImportTask = initialImportTask;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String getName() {
        return "Reconciliation of Payment data";
    }

    @Override
    protected Payments readPaymentData() throws IOException {
        return null;
    }

    @Override
    public void execute() {
        logger.info("Deleting all Payment data from database...");
        paymentRepository.clear();

        logger.info("Launching task '{}'...", initialImportTask.getName());
        initialImportTask.run();

        this.importStatistics = initialImportTask.importStatistics;
    }
}
