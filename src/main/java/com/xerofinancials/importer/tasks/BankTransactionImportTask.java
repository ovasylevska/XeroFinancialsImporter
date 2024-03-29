package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.BankTransaction;
import com.xero.models.accounting.BankTransactions;
import com.xerofinancials.importer.beans.ImportStatistics;
import com.xerofinancials.importer.dto.BankAccountDto;
import com.xerofinancials.importer.dto.BankTransactionDto;
import com.xerofinancials.importer.dto.ContactDto;
import com.xerofinancials.importer.dto.LineItemDto;
import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.repository.BankAccountRepository;
import com.xerofinancials.importer.repository.ContactRepository;
import com.xerofinancials.importer.repository.BankTransactionRepository;
import com.xerofinancials.importer.repository.LineItemRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BankTransactionImportTask extends ImportTask {
    private static final Logger logger = LoggerFactory.getLogger(BankTransactionImportTask.class);
    final BankTransactionRepository bankTransactionRepository;
    final ContactRepository contactRepository;
    final BankAccountRepository bankAccountRepository;
    final LineItemRepository lineItemRepository;

    private Integer bankTransactionsMaxEntityId;
    private Integer contactsMaxEntityId;
    private Integer bankAccountsMaxEntityId;
    private Integer lineItemsMaxEntityId;

    protected BankTransactionImportTask(
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository,
            final TokenStorage tokenStorage,
            final BankTransactionRepository bankTransactionRepository,
            final ContactRepository contactRepository,
            final BankAccountRepository bankAccountRepository,
            final LineItemRepository lineItemRepository,
            final EmailService emailService
    ) {
        super(emailService, tokenStorage, taskLaunchHistoryRepository);
        this.bankTransactionRepository = bankTransactionRepository;
        this.contactRepository = contactRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.lineItemRepository = lineItemRepository;
    }

    @Override
    public XeroDataType getDataType() {
        return XeroDataType.BANK_TRANSACTION;
    }

    @Override
    public void execute() {
        try {
            rememberExistingData();
            processBankTransactionData();
        } catch (XeroApiException e) {
            logXeroApiException(e);
            throw new RuntimeException("Failed to execute '" + getName() + "' task", e);
        } catch (Exception e) {
            logger.error("Exception while executing task", e);
            throw new RuntimeException("Failed to execute '" + getName() + "' task", e);
        }
    }

    protected abstract BankTransactions readBankTransactionData(final Counter pageCount, final Counter resultsCount) throws IOException;

    private void processBankTransactionData() throws IOException {
        final Counter pageCount = new Counter(1);
        final Counter resultsCount = new Counter(Integer.MAX_VALUE);
        final ImportStatistics importStatistics = new ImportStatistics();
        while(resultsCount.get() > 0) {
            final BankTransactions bankTransactionData = readBankTransactionData(pageCount, resultsCount);
            saveBankTransactionData(bankTransactionData);

            importStatistics.increaseNewBankTransactionsCount(bankTransactionData.getBankTransactions().size());
            resultsCount.set(bankTransactionData.getBankTransactions().size());
            pageCount.increment();
        }
        this.importStatistics = importStatistics;
    }

    private void rememberExistingData() {
        this.bankTransactionsMaxEntityId = bankTransactionRepository.getMaxEntityId().orElse(null);
        this.contactsMaxEntityId = contactRepository.getMaxEntityId().orElse(null);
        this.bankAccountsMaxEntityId = bankAccountRepository.getMaxEntityId().orElse(null);
        this.lineItemsMaxEntityId = lineItemRepository.getMaxEntityId().orElse(null);
    }

    private void saveBankTransactionData(BankTransactions data) {
        logger.info("Saving bank transaction data (size {}) ...", data.getBankTransactions().size());

        final List<BankTransactionDto> bankTransactionsData = data.getBankTransactions()
                .stream()
                .map(BankTransactionDto::new)
                .collect(Collectors.toList());
        bankTransactionRepository.save(bankTransactionsData);

        final List<ContactDto> contactsData = data.getBankTransactions()
                .stream()
                .map(BankTransaction::getContact)
                .map(ContactDto::new)
                .filter(c -> c.getContactId() != null)
                .distinct()
                .collect(Collectors.toList());
        contactRepository.saveNewContacts(contactsData);

        final List<BankAccountDto> bankAccountsData = data.getBankTransactions()
                .stream()
                .map(BankTransaction::getBankAccount)
                .map(BankAccountDto::new)
                .filter(ba -> ba.getBankAccountId() != null)
                .distinct()
                .collect(Collectors.toList());
        bankAccountRepository.saveNewBankAccounts(bankAccountsData);

        final List<LineItemDto> lineItemsData = data.getBankTransactions()
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

    @Override
    void rollback() {
        if (this.bankTransactionsMaxEntityId != null) {
            logger.info("Rollback Bank Transaction data...");
            bankTransactionRepository.delete(this.bankTransactionsMaxEntityId);
        }
        if (this.contactsMaxEntityId != null) {
            logger.info("Rollback Contact data...");
            contactRepository.delete(this.contactsMaxEntityId);
        }
        if (this.bankAccountsMaxEntityId != null) {
            logger.info("Rollback Bank Account data...");
            bankAccountRepository.delete(this.bankAccountsMaxEntityId);
        }
        if (this.lineItemsMaxEntityId != null) {
            logger.info("Rollback Line Item data...");
            lineItemRepository.delete(this.lineItemsMaxEntityId);
        }
    }
}
