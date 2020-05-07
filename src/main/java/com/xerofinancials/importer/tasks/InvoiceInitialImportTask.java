package com.xerofinancials.importer.tasks;

import com.xero.models.accounting.Invoices;
import com.xerofinancials.importer.repository.InvoiceLineItemRepository;
import com.xerofinancials.importer.repository.InvoiceRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InvoiceInitialImportTask extends InvoiceImportTask {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceInitialImportTask.class);
    private static final int UNIT_DECIMAL_PLACES = 4;
    private final XeroApiWrapper xeroApiWrapper;

    protected InvoiceInitialImportTask(
            final EmailService emailService,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final InvoiceRepository invoiceRepository,
            final InvoiceLineItemRepository lineItemRepository,
            final XeroApiWrapper xeroApiWrapper
    ) {
        super(emailService, tokenStorage, taskLaunchHistoryRepository, invoiceRepository, lineItemRepository);
        this.xeroApiWrapper = xeroApiWrapper;
    }

    public String getName() {
        return "Initial import of Invoice data";
    }

    @Override
    protected Invoices readInvoiceData(
            final Counter pageCount,
            final Counter resultsCount
    ) throws IOException {
        logger.info("Retrieving next batch of data (page {}) ...", pageCount.get());

        final Invoices invoicesData = xeroApiWrapper.executeApiCall(
                (accountingApi, accessToken, tenantId) -> accountingApi.getInvoices(
                        accessToken,
                        tenantId,
                        null,
                        null,
                        "Date",
                        null,
                        null,
                        null,
                        null,
                        pageCount.get(),
                        true,
                        false,
                        UNIT_DECIMAL_PLACES
                ));
        return invoicesData;
    }

}
