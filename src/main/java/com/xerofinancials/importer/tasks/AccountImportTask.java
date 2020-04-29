package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.Accounts;
import com.xerofinancials.importer.beans.ImportStatistics;
import com.xerofinancials.importer.dto.AccountDto;
import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.repository.AccountRepository;
import com.xerofinancials.importer.repository.TaskLaunchHistoryRepository;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AccountImportTask extends ImportTask {
    private static final Logger logger = LoggerFactory.getLogger(AccountImportTask.class);
    final AccountRepository accountRepository;
    private Integer accountsMaxEntityId;

    protected AccountImportTask(
            final EmailService emailService,
            final XeroApiWrapper xeroApiWrapper,
            final AccountRepository accountRepository,
            final TokenStorage tokenStorage,
            final TaskLaunchHistoryRepository taskLaunchHistoryRepository
    ) {
        super(emailService, tokenStorage, taskLaunchHistoryRepository);
        this.accountRepository = accountRepository;
    }

    @Override
    public XeroDataType getDataType() {
        return XeroDataType.ACCOUNT;
    }

    protected abstract Accounts readAccountData() throws IOException;

    @Override
    protected void execute() {
        try {
            rememberExistingData();
            processAccountData();
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
        if (this.accountsMaxEntityId != null) {
            logger.info("Rollback Account data...");
            accountRepository.delete(this.accountsMaxEntityId);
        }
    }

    private void rememberExistingData() {
        this.accountsMaxEntityId = accountRepository.getMaxEntityId().orElse(null);
    }

    private void processAccountData() throws IOException {
        final ImportStatistics importStatistics = new ImportStatistics();
        final Accounts accountData = readAccountData();
        saveAccountData(accountData);
        importStatistics.increaseNewAccountsCount(accountData.getAccounts().size());
        this.importStatistics = importStatistics;
    }

    private void saveAccountData(Accounts data) {
        logger.info("Saving account data (size {}) ...", data.getAccounts().size());

        final List<AccountDto> accountsData = data.getAccounts()
                .stream()
                .map(AccountDto::new)
                .collect(Collectors.toList());
        accountRepository.save(accountsData);
    }
}
