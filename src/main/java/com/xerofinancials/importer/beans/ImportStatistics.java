package com.xerofinancials.importer.beans;

public class ImportStatistics {
    private int newBankTransactionsCount;

    public int getNewBankTransactionsCount() {
        return newBankTransactionsCount;
    }

    public void increaseNewBankTransactionsCount(final int newBankTransactionsCount) {
        this.newBankTransactionsCount += newBankTransactionsCount;
    }

    @Override
    public String toString() {
        return "Import Statistics : Bank Transactions count = " + newBankTransactionsCount + "";
    }
}
