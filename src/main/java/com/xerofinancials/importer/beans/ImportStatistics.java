package com.xerofinancials.importer.beans;

public class ImportStatistics {
    private int newBankTransactionsCount;
    private int newAccountsCount;

    public int getNewBankTransactionsCount() {
        return newBankTransactionsCount;
    }

    public void increaseNewBankTransactionsCount(final int newBankTransactionsCount) {
        this.newBankTransactionsCount += newBankTransactionsCount;
    }

    public void increaseNewAccountsCount(final int newAccountsCount) {
        this.newAccountsCount += newAccountsCount;
    }

    @Override
    public String toString() {
        return "ImportStatistics : {" +
                "Bank Transactions Count=" + newBankTransactionsCount +
                ", Accounts Count=" + newAccountsCount +
                '}';
    }
}
