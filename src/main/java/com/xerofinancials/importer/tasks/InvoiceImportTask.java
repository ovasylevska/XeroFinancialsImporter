package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.Invoices;
import com.xerofinancials.importer.beans.ImportStatistics;
import com.xerofinancials.importer.dto.InvoiceDto;
import com.xerofinancials.importer.dto.LineItemDto;
import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.repository.InvoiceLineItemRepository;
import com.xerofinancials.importer.repository.InvoiceRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class InvoiceImportTask extends ImportTask {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceImportTask.class);
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineItemRepository lineItemRepository;

    private Integer invoicesMaxEntityId;
    private Integer lineItemsMaxEntityId;

    protected InvoiceImportTask(
            final EmailService emailService,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final InvoiceRepository invoiceRepository,
            final InvoiceLineItemRepository lineItemRepository
    ) {
        super(emailService, tokenStorage, taskLaunchHistoryRepository);
        this.invoiceRepository = invoiceRepository;
        this.lineItemRepository = lineItemRepository;
    }

    public XeroDataType getDataType() {
        return XeroDataType.INVOICE;
    }

    protected abstract Invoices readInvoiceData(final Counter pageCount, final Counter resultsCount) throws IOException;

    @Override
    protected void execute() {
        try {
            rememberExistingData();
            processInvoiceData();
        } catch (XeroApiException e) {
            logXeroApiException(e);
            throw new RuntimeException("Failed to execute '" + getName() + "' task", e);
        } catch (Exception e) {
            logger.error("Exception while executing task", e);
            throw new RuntimeException("Failed to execute '" + getName() + "' task", e);
        }
    }

    @Override
    void rollback() {
        if (this.invoicesMaxEntityId != null) {
            logger.info("Rollback Invoice data...");
            invoiceRepository.delete(this.invoicesMaxEntityId);
        }
        if (this.lineItemsMaxEntityId != null) {
            logger.info("Rollback Line Item data...");
            lineItemRepository.delete(this.lineItemsMaxEntityId);
        }
    }

    private void rememberExistingData() {
        this.invoicesMaxEntityId = invoiceRepository.getMaxEntityId().orElse(null);
        this.lineItemsMaxEntityId = lineItemRepository.getMaxEntityId().orElse(null);
    }

    private void processInvoiceData() throws IOException {
        final Counter pageCount = new Counter(1);
        final Counter resultsCount = new Counter(Integer.MAX_VALUE);
        final ImportStatistics importStatistics = new ImportStatistics();
        while (resultsCount.get() > 0) {
            final Invoices invoiceData = readInvoiceData(pageCount, resultsCount);
            saveInvoiceData(invoiceData);

            importStatistics.increaseNewInvoicesCount(invoiceData.getInvoices().size());
            resultsCount.set(invoiceData.getInvoices().size());
            pageCount.increment();
        }
        this.importStatistics = importStatistics;
    }

    private void saveInvoiceData(Invoices data) {
        logger.info("Saving invoice data (size {}) ...", data.getInvoices().size());

        final List<InvoiceDto> invoicesData = data.getInvoices()
                .stream()
                .map(InvoiceDto::new)
                .collect(Collectors.toList());
        invoiceRepository.save(invoicesData);

        final List<LineItemDto> lineItemsData = data.getInvoices()
                .stream()
                .flatMap(b -> {
                    final List<LineItemDto> lineItems = new ArrayList<>();
                    b.getLineItems().forEach(l -> lineItems.add(new LineItemDto(l, b)));
                    return lineItems.stream();
                })
                .filter(li -> li.getLineItemId() != null)
                .distinct()
                .collect(Collectors.toList());
        lineItemRepository.save(lineItemsData);
    }

}
