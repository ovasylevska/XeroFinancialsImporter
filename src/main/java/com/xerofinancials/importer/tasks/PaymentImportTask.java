package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.Payments;
import com.xerofinancials.importer.beans.ImportStatistics;
import com.xerofinancials.importer.dto.PaymentDto;
import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.repository.PaymentRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PaymentImportTask extends ImportTask {
    private static final Logger logger = LoggerFactory.getLogger(PaymentImportTask.class);
    private final PaymentRepository paymentRepository;
    private Integer paymentsMaxEntityId;

    protected PaymentImportTask(
            final EmailService emailService,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final PaymentRepository paymentRepository
    ) {
        super(emailService, tokenStorage, taskLaunchHistoryRepository);
        this.paymentRepository = paymentRepository;
    }

    @Override
    public XeroDataType getDataType() {
        return XeroDataType.PAYMENT;
    }

    protected abstract Payments readPaymentData() throws IOException;

    @Override
    protected void execute() {
        try {
            rememberExistingData();
            processPaymentData();
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
        if (this.paymentsMaxEntityId != null) {
            logger.info("Rollback Payment data...");
            paymentRepository.delete(this.paymentsMaxEntityId);
        }
    }

    private void rememberExistingData() {
        this.paymentsMaxEntityId = paymentRepository.getMaxEntityId().orElse(null);
    }

    private void processPaymentData() throws IOException {
        final ImportStatistics importStatistics = new ImportStatistics();
        final Payments paymentData = readPaymentData();
        saveInvoiceData(paymentData);

        importStatistics.increaseNewInvoicesCount(paymentData.getPayments().size());
        this.importStatistics = importStatistics;
    }

    private void saveInvoiceData(Payments data) {
        logger.info("Saving payment data (size {}) ...", data.getPayments().size());

        final List<PaymentDto> invoicesData = data.getPayments()
                .stream()
                .map(PaymentDto::new)
                .collect(Collectors.toList());
        paymentRepository.save(invoicesData);
    }
}
