package com.xerofinancials.importer.beans;

public class ImportStatistics {
    private int newBankTransactionsCount;
    private int newAccountsCount;
    private int newInvoicesCount;
    private int newPaymentsCount;

    public int getNewBankTransactionsCount() {
        return newBankTransactionsCount;
    }

    public void increaseNewBankTransactionsCount(final int newBankTransactionsCount) {
        this.newBankTransactionsCount += newBankTransactionsCount;
    }

    public void increaseNewAccountsCount(final int newAccountsCount) {
        this.newAccountsCount += newAccountsCount;
    }

    public void increaseNewInvoicesCount(final int newInvoiceCount) {
        this.newInvoicesCount += newInvoiceCount;
    }

    public void increaseNewPaymentsCount(final int newPaymentCount) {
        this.newPaymentsCount += newPaymentCount;
    }

    @Override
    public String toString() {
        return "ImportStatistics : {" +
                "Bank Transactions Count = " + newBankTransactionsCount +
                ", Accounts Count = " + newAccountsCount +
                ", Invoices Count = " + newInvoicesCount +
                ", Payments Count = " + newPaymentsCount +
                '}';
    }
}
