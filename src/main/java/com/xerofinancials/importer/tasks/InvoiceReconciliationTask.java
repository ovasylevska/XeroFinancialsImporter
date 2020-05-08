package com.xerofinancials.importer.tasks;

import com.xero.models.accounting.Invoices;
import com.xerofinancials.importer.repository.InvoiceLineItemRepository;
import com.xerofinancials.importer.repository.InvoiceRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InvoiceReconciliationTask extends InvoiceImportTask {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceReconciliationTask.class);
    private final InvoiceInitialImportTask initialImportTask;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineItemRepository lineItemRepository;

    protected InvoiceReconciliationTask(
            final EmailService emailService,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final InvoiceRepository invoiceRepository,
            final InvoiceLineItemRepository lineItemRepository,
            final InvoiceInitialImportTask initialImportTask
    ) {
        super(emailService, tokenStorage, taskLaunchHistoryRepository, invoiceRepository, lineItemRepository);
        this.initialImportTask = initialImportTask;
        this.invoiceRepository = invoiceRepository;
        this.lineItemRepository = lineItemRepository;
    }

    @Override
    public String getName() {
        return "Reconciliation of Invoice data";
    }

    @Override
    protected Invoices readInvoiceData(final Counter pageCount, final Counter resultsCount) throws IOException {
        return null;
    }

    @Override
    public void execute() {
        logger.info("Deleting all Invoice data from database...");
        invoiceRepository.clear();

        logger.info("Deleting all Invoice Line Item data from database...");
        lineItemRepository.clear();

        logger.info("Launching task '{}'...", initialImportTask.getName());
        initialImportTask.run();

        this.importStatistics = initialImportTask.importStatistics;
    }
}
