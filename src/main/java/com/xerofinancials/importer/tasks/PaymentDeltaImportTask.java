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
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;

@Component
public class PaymentDeltaImportTask extends PaymentImportTask {
    private static final Logger logger = LoggerFactory.getLogger(PaymentDeltaImportTask.class);
    private final XeroApiWrapper xeroApiWrapper;

    protected PaymentDeltaImportTask(
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
        return "Delta import of Payment data";
    }

    @Override
    protected Payments readPaymentData() throws IOException {
        final OffsetDateTime modifiedSinceDate = getModifiedSinceDate();
        logger.info("Retrieving next batch of data since {} ...", modifiedSinceDate);
        final Payments paymentsData = xeroApiWrapper.executeApiCall(
                (accountingApi, accessToken, tenantId) -> accountingApi.getPayments(
                        accessToken,
                        tenantId,
                        modifiedSinceDate,
                        null,
                        "Date"
                )
        );
        return paymentsData;
    }
}
