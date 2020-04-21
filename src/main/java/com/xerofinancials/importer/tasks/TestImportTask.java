package com.xerofinancials.importer.tasks;

import com.xero.api.XeroApiException;
import com.xero.models.accounting.Account;
import com.xero.models.accounting.AccountType;
import com.xero.models.accounting.Accounts;
import com.xero.models.accounting.BankTransaction;
import com.xero.models.accounting.BankTransactions;
import com.xero.models.accounting.Contact;
import com.xero.models.accounting.Contacts;
import com.xero.models.accounting.Item;
import com.xero.models.accounting.Items;
import com.xero.models.accounting.LineItem;
import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.service.EmailService;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

//todo: delete
public class TestImportTask extends ImportTask{
    private static final Logger logger = LoggerFactory.getLogger(TestImportTask.class);
    private final XeroApiWrapper xeroApiWrapper;
    private final TokenStorage tokenStorage;
    private final EmailService emailService;

    public TestImportTask(
            final XeroApiWrapper xeroApiWrapper,
            final TokenStorage tokenStorage,
            final EmailService emailService
    ) {
        super(emailService);
        this.xeroApiWrapper = xeroApiWrapper;
        this.tokenStorage = tokenStorage;
        this.emailService = emailService;
    }

    public void execute() {
        try {
            if (!tokenStorage.isAuthentificated()) {
                throw new RuntimeException("Application is not Authenticated!");
            }
            addBankTransaction();
            readBankTransactions();
        } catch (XeroApiException e) {
            logger.error("Exception while executing task", e);
            logXeroApiException(e);
        } catch (IOException e) {
            logger.error("Exception while executing task", e);
        }
    }

    @Override
    public String getName() {
        return "Add test data";
    }

    @Override
    public XeroDataType getDataType() {
        return XeroDataType.BANK_TRANSACTION;
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

            LineItem lineItem1 = new LineItem();
            lineItem1.setDescription("Yearly Bank Account Fee");
            lineItem1.setUnitAmount(100.00f);
            lineItem1.setAccountCode("404");
            LineItem lineItem2 = new LineItem();
            lineItem2.setDescription("GB1-White");
            lineItem2.setUnitAmount(99.00f);
            lineItem2.setAccountCode("404");
            item.setLineItems(Arrays.asList(lineItem1, lineItem2));

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

    private UUID addContact() throws IOException {
        Contact contact = new Contact();
        contact.setName("BNZ");

        Contacts contacts = new Contacts();
        contacts.addContactsItem(contact);

        Contacts createdContacts = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.createContacts(accessToken, tenantId, contacts, true));
        return createdContacts.getContacts().get(0).getContactID();
    }

    private UUID getContact() throws IOException {
        Contacts contacts = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.getContacts(accessToken, tenantId, null, null, null, null, null, false));
        return contacts.getContacts().get(contacts.getContacts().size() - 1).getContactID();
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
        account.setName("Cheque Account 222");
        account.setType(AccountType.BANK);
        account.setBankAccountNumber("222-222-1234567");

        Accounts createdAccount = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.createAccount(accessToken, tenantId, account));
        return createdAccount.getAccounts().get(0).getAccountID();
    }

    private UUID getAccount() throws IOException {
        Accounts accounts = xeroApiWrapper.executeApiCall((accountingApi, accessToken, tenantId) -> accountingApi.getAccounts(accessToken, tenantId, null, null, null));
        return accounts.getAccounts().get(0).getAccountID();
    }
}
