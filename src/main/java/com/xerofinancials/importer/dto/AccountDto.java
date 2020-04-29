package com.xerofinancials.importer.dto;

import com.xero.models.accounting.Account;
import com.xerofinancials.importer.utils.DateUtils;
import org.threeten.bp.OffsetDateTime;

import java.time.LocalDateTime;

public class AccountDto {
    private String accountId;
    private String accountCode;
    private String name;
    private String type;
    private String bankAccountNumber;
    private String status;
    private String description;
    private String bankAccountType;
    private String currencyCode;
    private String taxType;
    private int enablePaymentsToAccount;
    private int showInExpenseClaims;
    private String propertyClass;
    private String systemAccount;
    private String reportingCode;
    private String reportingCodeName;
    private int hasAttachments;
    private LocalDateTime updatedDateUtc;
    private int addToWatchList;

    public AccountDto(Account xeroAccount) {
        this.accountId = xeroAccount.getAccountID().toString();
        this.accountCode = xeroAccount.getCode();
        this.name = xeroAccount.getName();
        if (xeroAccount.getType() != null) {
            this.type = xeroAccount.getType().getValue();
        }
        this.bankAccountNumber = xeroAccount.getBankAccountNumber();
        if (xeroAccount.getStatus() != null) {
            this.status = xeroAccount.getStatus().getValue();
        }
        this.description = xeroAccount.getDescription();
        if (xeroAccount.getBankAccountType() != null) {
            this.bankAccountType = xeroAccount.getBankAccountType().getValue();
        }
        if (xeroAccount.getCurrencyCode() != null) {
            this.currencyCode = xeroAccount.getCurrencyCode().getValue();
        }
        this.taxType = xeroAccount.getTaxType();
        this.enablePaymentsToAccount = xeroAccount.getEnablePaymentsToAccount() ? 1 : 0;
        this.showInExpenseClaims = xeroAccount.getShowInExpenseClaims() ? 1 : 0;
        if (xeroAccount.getPropertyClass() != null) {
            this.propertyClass = xeroAccount.getPropertyClass().getValue();
        }
        if (xeroAccount.getSystemAccount() != null) {
            this.systemAccount = xeroAccount.getSystemAccount().getValue();
        }
        this.reportingCode = xeroAccount.getReportingCode();
        this.reportingCodeName = xeroAccount.getReportingCodeName();
        this.hasAttachments = xeroAccount.getHasAttachments() ? 1 : 0;
        if (xeroAccount.getUpdatedDateUTC() != null) {
            final OffsetDateTime updatedDateUTC = xeroAccount.getUpdatedDateUTC();
            this.updatedDateUtc = DateUtils.convertToUtc(updatedDateUTC);
        }
        this.addToWatchList = xeroAccount.getAddToWatchlist() ? 1 : 0;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getTaxType() {
        return taxType;
    }

    public int getEnablePaymentsToAccount() {
        return enablePaymentsToAccount;
    }

    public int getShowInExpenseClaims() {
        return showInExpenseClaims;
    }

    public String getPropertyClass() {
        return propertyClass;
    }

    public String getSystemAccount() {
        return systemAccount;
    }

    public String getReportingCode() {
        return reportingCode;
    }

    public String getReportingCodeName() {
        return reportingCodeName;
    }

    public int getHasAttachments() {
        return hasAttachments;
    }

    public LocalDateTime getUpdatedDateUtc() {
        return updatedDateUtc;
    }

    public int getAddToWatchList() {
        return addToWatchList;
    }
}
