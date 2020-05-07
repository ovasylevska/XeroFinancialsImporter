package com.xerofinancials.importer.tasks;

import com.xero.models.accounting.Payments;
import com.xerofinancials.importer.repository.PaymentRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PaymentInitialImportTask extends PaymentImportTask {
    private static final Logger logger = LoggerFactory.getLogger(PaymentInitialImportTask.class);
    private final XeroApiWrapper xeroApiWrapper;

    protected PaymentInitialImportTask(
            final EmailService emailService,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final PaymentRepository paymentRepository,
            final XeroApiWrapper xeroApiWrapper
    ) {
        super(emailService, tokenStorage, taskLaunchHistoryRepository, paymentRepository);
        this.xeroApiWrapper = xeroApiWrapper;
    }

    @Override
    public String getName() {
        return "Initial import of Payment data";
    }

    @Override
    protected Payments readPaymentData() throws IOException {
        logger.info("Retrieving all data ...");

        final Payments paymentsData = xeroApiWrapper.executeApiCall(
                (accountingApi, accessToken, tenantId) -> accountingApi.getPayments(
                        accessToken,
                        tenantId,
                        null,
                        null,
                        "Date"
                )
        );
        return paymentsData;
    }
}
