package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.api.client.AccountingApi;
import com.xero.models.accounting.Account;
import com.xero.models.accounting.AccountType;
import com.xero.models.accounting.Accounts;
import com.xero.models.accounting.BankTransaction;
import com.xero.models.accounting.BankTransactions;
import com.xero.models.accounting.Contact;
import com.xero.models.accounting.Contacts;
import com.xero.models.accounting.Element;
import com.xero.models.accounting.Item;
import com.xero.models.accounting.Items;
import com.xero.models.accounting.LineItem;
import com.xero.models.accounting.ValidationError;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class TestImportTask {
    private static final Logger logger = LoggerFactory.getLogger(TestImportTask.class);
    private final XeroApiWrapper xeroApiWrapper;

    public TestImportTask(final XeroApiWrapper xeroApiWrapper) {
        this.xeroApiWrapper = xeroApiWrapper;
    }

    public void execute() throws IOException {
        try {
            addBankTransaction();
            readBankTransactions();
        } catch (XeroApiException e) {
            logger.error("Exception while executing task", e);
            logXeroApiException(e);
        }
    }

    private void addBankTransaction() throws IOException {
        try {
            BankTransactions bankTransactions = new BankTransactions();
            BankTransaction item = new BankTransaction();
            item.setType(BankTransaction.TypeEnum.SPEND);

            UUID contactUuid = getContact();
            logger.info(contactUuid.toString());
            Contact contact = new Contact();
            contact.setContactID(contactUuid);
            item.setContact(contact);

            LineItem lineItem = new LineItem();
            lineItem.setDescription("Yearly Bank Account Fee");
            lineItem.setUnitAmount(20.00f);
            lineItem.setAccountCode("404");
            item.setLineItems(Arrays.asList(lineItem));

            UUID accountUuid = getAccount();
            logger.info(accountUuid.toString());
            Account account = new Account();
            account.setAccountID(accountUuid);
            item.setBankAccount(account);

            bankTransactions.addBankTransactionsItem(item);

            BankTransactions createdBankTransactions = xeroApiWrapper.executeApiCall((accountingApi1, accessToken1, tenantId1) -> accountingApi1.createBankTransactions(
                    accessToken1,
                    tenantId1,
                    bankTransactions,
                    true,
                    4
            ));
            logger.info("Created bank transaction uuid : " + createdBankTransactions.getBankTransactions().get(0).getBankTransactionID().toString());
        } catch (XeroApiException e) {
            logger.error("Exception while executing task", e);
            logXeroApiException(e);
        }
    }

    private void readBankTransactions() throws IOException {
        BankTransactions readBankTransactions = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.getBankTransactions(
                accessToken,
                tenantId,
                null,
                null,
                null,
                null,
                4
        ));
        logger.info("Number of bank transactions : {}", readBankTransactions.getBankTransactions().size());
    }

    private void logXeroApiException(XeroApiException e) {
        logger.error("Xero Api Exception: " + e.getResponseCode());
        for (Element item : e.getError().getElements()) {
            for (ValidationError err : item.getValidationErrors()) {
                logger.error("Validation error : " + err.getMessage());
            }
        }
    }

    private UUID addContact(
            String accessToken,
            String tenantId,
            AccountingApi accountingApi
    ) throws IOException {
        Contact contact = new Contact();
        contact.setName("ABC Limited");

        Contacts contacts = new Contacts();
        contacts.addContactsItem(contact);

        Contacts createdContacts = xeroApiWrapper.executeApiCall((accountingApi1, accessToken1, tenantId1) -> accountingApi1.createContacts(accessToken1, tenantId1, contacts, true));
        return createdContacts.getContacts().get(0).getContactID();
    }

    private UUID getContact() throws IOException {
        Contacts contacts = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.getContacts(accessToken, tenantId, null, null, null, null, null, false));
        return contacts.getContacts().get(0).getContactID();
    }

    private UUID addLineItem() throws IOException {
        Item item = new Item();
        item.setCode("Item-1");

        Items items = new Items();
        items.addItemsItem(item);

        Items createdItems = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.createItems(accessToken, tenantId, items, true, 4));
        return createdItems.getItems().get(0).getItemID();
    }

    private UUID getLineItem() throws IOException {
        Items items = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.getItems(accessToken, tenantId, null, null, null, 4));
        return items.getItems().get(0).getItemID();
    }

    private UUID addAccount() throws IOException {
        Account account = new Account();
        account.setName("Cheque Account");
        account.setType(AccountType.BANK);
        account.setBankAccountNumber("121-121-1234567");

        Accounts createdAccount = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.createAccount(accessToken, tenantId, account));
        return createdAccount.getAccounts().get(0).getAccountID();
    }

    private UUID getAccount() throws IOException {
        Accounts accounts = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.getAccounts(accessToken, tenantId, null, null, null));
        return accounts.getAccounts().get(0).getAccountID();
    }
}
